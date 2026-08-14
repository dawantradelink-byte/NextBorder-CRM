package com.example.util

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.data.University
import com.example.data.UniversityDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import org.json.JSONArray
import org.json.JSONObject

object WebAdminServer {

    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    private var serverJob: Job? = null
    const val PORT = 8080

    fun startServer(context: Context, dao: UniversityDao, onStatusChange: (Boolean, String) -> Unit) {
        if (isRunning) {
            onStatusChange(true, getLocalIpAddress(context))
            return
        }

        serverJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(PORT)
                isRunning = true
                val ip = getLocalIpAddress(context)
                
                withContext(Dispatchers.Main) {
                    onStatusChange(true, "http://$ip:$PORT")
                }

                while (isRunning && serverSocket?.isClosed == false) {
                    val clientSocket = serverSocket?.accept() ?: break
                    handleClient(clientSocket, dao)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isRunning = false
                withContext(Dispatchers.Main) {
                    onStatusChange(false, "Server stopped: ${e.message}")
                }
            }
        }
    }

    fun stopServer(onStatusChange: (Boolean, String) -> Unit) {
        isRunning = false
        try {
            serverSocket?.close()
            serverSocket = null
            serverJob?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onStatusChange(false, "Server Offline")
    }

    fun isServerRunning(): Boolean = isRunning

    fun getLocalIpAddress(context: Context): String {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager != null) {
                val ipAddress = wifiManager.connectionInfo.ipAddress
                if (ipAddress != 0) {
                    return Formatter.formatIpAddress(ipAddress)
                }
            }
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.hostAddress?.contains(':') == false) {
                        return address.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "127.0.0.1"
    }

    private suspend fun handleClient(socket: Socket, dao: UniversityDao) {
        withContext(Dispatchers.IO) {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = PrintWriter(socket.getOutputStream(), true)

                val requestLine = reader.readLine() ?: return@withContext
                val tokens = requestLine.split(" ")
                val method = tokens.getOrNull(0) ?: "GET"
                val path = tokens.getOrNull(1) ?: "/"

                if (path == "/" || path == "/index.html") {
                    val html = buildWebDashboardHtml(dao)
                    sendHttpResponse(writer, "200 OK", "text/html", html)
                } else if (path == "/api/universities") {
                    val list = dao.getAllActive().first()
                    val jsonArray = JSONArray()
                    list.forEach { u ->
                        jsonArray.put(JSONObject().apply {
                            put("id", u.id)
                            put("name", u.name)
                            put("country", u.country)
                            put("city", u.city)
                            put("contactName", u.contactName)
                            put("email", u.email)
                            put("status", u.partnershipStatus)
                            put("priority", u.priority)
                            put("counselor", u.assignedCounselor)
                        })
                    }
                    sendHttpResponse(writer, "200 OK", "application/json", jsonArray.toString())
                } else {
                    sendHttpResponse(writer, "404 Not Found", "text/plain", "404 Not Found")
                }
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendHttpResponse(writer: PrintWriter, status: String, contentType: String, content: String) {
        writer.println("HTTP/1.1 $status")
        writer.println("Content-Type: $contentType; charset=UTF-8")
        writer.println("Content-Length: ${content.toByteArray(Charsets.UTF_8).size}")
        writer.println("Access-Control-Allow-Origin: *")
        writer.println("Connection: close")
        writer.println()
        writer.println(content)
        writer.flush()
    }

    private suspend fun buildWebDashboardHtml(dao: UniversityDao): String {
        val list = dao.getAllActive().first()
        val total = list.size
        val prospects = list.count { it.partnershipStatus == "Prospect" }
        val contacted = list.count { it.partnershipStatus == "Contacted" }
        val partnered = list.count { it.partnershipStatus == "Partnered" }

        val rowsBuilder = StringBuilder()
        list.forEach { u ->
            val statusBadge = when (u.partnershipStatus) {
                "Partnered" -> "<span class='badge badge-success'>Partnered</span>"
                "Contacted" -> "<span class='badge badge-warning'>Contacted</span>"
                "In Discussion" -> "<span class='badge badge-info'>In Discussion</span>"
                else -> "<span class='badge badge-secondary'>${u.partnershipStatus}</span>"
            }

            rowsBuilder.append("""
                <tr>
                    <td><b>${u.id}</b></td>
                    <td><b>${u.name}</b><br><small style='color:#9CA3AF;'>${u.website}</small></td>
                    <td>${u.country} (${u.city})</td>
                    <td>${u.contactName}<br><small style='color:#60A5FA;'>${u.email}</small></td>
                    <td>$statusBadge</td>
                    <td><span class='priority-${u.priority.lowercase()}'>${u.priority}</span></td>
                    <td>${u.assignedCounselor}</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Next Border CRM - Laptop Web Admin Dashboard</title>
                <style>
                    body { font-family: 'Segoe UI', system-ui, -apple-system, sans-serif; background-color: #0F172A; color: #F8FAFC; margin: 0; padding: 24px; }
                    .header { display: flex; justify-content: space-between; align-items: center; background: #1E293B; padding: 20px 28px; border-radius: 16px; border: 1px solid #334155; margin-bottom: 24px; box-shadow: 0 10px 25px rgba(0,0,0,0.3); }
                    .title h1 { margin: 0; font-size: 24px; color: #FFFFFF; font-weight: 700; }
                    .title p { margin: 4px 0 0 0; color: #FF8A00; font-size: 13px; font-weight: 600; }
                    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px; }
                    .stat-card { background: #1E293B; padding: 20px; border-radius: 12px; border: 1px solid #334155; text-align: center; }
                    .stat-number { font-size: 32px; font-weight: 800; color: #FF2E93; margin-top: 4px; }
                    .stat-label { font-size: 12px; color: #94A3B8; text-transform: uppercase; letter-spacing: 1px; font-weight: 600; }
                    .table-card { background: #1E293B; border-radius: 16px; border: 1px solid #334155; padding: 24px; box-shadow: 0 10px 25px rgba(0,0,0,0.3); }
                    table { width: 100%; border-collapse: collapse; margin-top: 16px; font-size: 14px; }
                    th, td { padding: 14px 16px; text-align: left; border-bottom: 1px solid #334155; }
                    th { background: #0F172A; color: #94A3B8; font-weight: 600; text-transform: uppercase; font-size: 11px; letter-spacing: 0.5px; }
                    tr:hover { background: rgba(255,255,255,0.03); }
                    .badge { padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-block; }
                    .badge-success { background: rgba(16, 185, 129, 0.2); color: #10B981; border: 1px solid #10B981; }
                    .badge-warning { background: rgba(255, 138, 0, 0.2); color: #FF8A00; border: 1px solid #FF8A00; }
                    .badge-info { background: rgba(96, 165, 250, 0.2); color: #60A5FA; border: 1px solid #60A5FA; }
                    .badge-secondary { background: rgba(148, 163, 184, 0.2); color: #94A3B8; border: 1px solid #94A3B8; }
                    .priority-high { color: #EF4444; font-weight: 700; }
                    .priority-medium { color: #F59E0B; font-weight: 700; }
                    .priority-low { color: #10B981; font-weight: 700; }
                    .btn { background: #FF2E93; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 700; cursor: pointer; text-decoration: none; display: inline-block; }
                    .btn:hover { background: #e02680; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="title">
                        <h1>Next Border CRM — Web Admin Dashboard</h1>
                        <p>Real-Time Laptop Management Portal • CEO Ishak Dewan</p>
                    </div>
                    <div>
                        <button onclick="location.reload()" class="btn">Refresh Data</button>
                    </div>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Total University Leads</div>
                        <div class="stat-number" style="color: #60A5FA;">$total</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Active Prospects</div>
                        <div class="stat-number" style="color: #94A3B8;">$prospects</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-card-label" style="font-size:12px; color:#94A3B8; font-weight:600; text-transform:uppercase;">Outreach Contacted</div>
                        <div class="stat-number" style="color: #FF8A00;">$contacted</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Signed Partners</div>
                        <div class="stat-number" style="color: #10B981;">$partnered</div>
                    </div>
                </div>

                <div class="table-card">
                    <h2 style="margin-top:0; font-size:18px;">University Partnership Pipeline (${list.size} Total)</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>University Name</th>
                                <th>Location</th>
                                <th>Contact Person</th>
                                <th>Status</th>
                                <th>Priority</th>
                                <th>Assigned Counselor</th>
                            </tr>
                        </thead>
                        <tbody>
                            $rowsBuilder
                        </tbody>
                    </table>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
