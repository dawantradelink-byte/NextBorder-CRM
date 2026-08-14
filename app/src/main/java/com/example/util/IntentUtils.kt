package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object IntentUtils {

    fun launchWhatsApp(context: Context, phoneNumber: String, initialMessage: String = "") {
        try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val encodedMsg = URLEncoder.encode(initialMessage, "UTF-8")
            val url = if (cleanNumber.isNotBlank()) {
                "https://wa.me/$cleanNumber?text=$encodedMsg"
            } else {
                "https://wa.me/447700900077?text=$encodedMsg"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp application or browser not found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchEmail(context: Context, toEmail: String, subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(intent, "Send email using..."))
        } catch (e: Exception) {
            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchPhoneCall(context: Context, phoneNumber: String) {
        try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch phone dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchWebBrowser(context: Context, url: String) {
        try {
            var formattedUrl = url.trim()
            if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                formattedUrl = "https://$formattedUrl"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open browser for URL: $url", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchGoogleCalendar(context: Context, title: String, description: String, timestamp: Long = System.currentTimeMillis()) {
        try {
            val startTime = if (timestamp > 0) timestamp else System.currentTimeMillis() + 86400000L
            val endTime = startTime + 3600000L
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = Uri.parse("content://com.android.calendar/events")
                putExtra("title", title)
                putExtra("description", description)
                putExtra("beginTime", startTime)
                putExtra("endTime", endTime)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to Google Calendar Web
            try {
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val webUrl = "https://calendar.google.com/calendar/render?action=TEMPLATE&text=$encodedTitle"
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not launch Calendar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun shareFile(context: Context, file: java.io.File, title: String = "Export CSV", mimeType: String = "text/csv") {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not share file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
