package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessSettings
import com.example.ui.components.GlassCard
import com.example.util.CsvUtils
import com.example.util.IntentUtils
import com.example.viewmodel.CrmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CrmViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentSettings by viewModel.businessSettings.collectAsState()
    val universities by viewModel.activeUniversities.collectAsState()

    val securityManager = viewModel.securityManager

    var agencyName by remember(currentSettings) { mutableStateOf(currentSettings.agencyName) }
    var ceoName by remember(currentSettings) { mutableStateOf(currentSettings.ceoName) }
    var officialEmail by remember(currentSettings) { mutableStateOf(currentSettings.officialEmail) }
    var whatsappNumber by remember(currentSettings) { mutableStateOf(currentSettings.whatsappNumber) }
    var emailSignature by remember(currentSettings) { mutableStateOf(currentSettings.emailSignature) }
    var defaultTone by remember(currentSettings) { mutableStateOf(currentSettings.defaultTone) }
    var defaultCommission by remember(currentSettings) { mutableStateOf(currentSettings.defaultCommissionModel) }
    var website by remember(currentSettings) { mutableStateOf(currentSettings.website) }
    var isDarkTheme by remember(currentSettings) { mutableStateOf(currentSettings.isDarkTheme) }

    var appLockEnabled by remember { mutableStateOf(securityManager?.isAppLockEnabled ?: false) }
    var lockTimeoutMinutes by remember { mutableStateOf(securityManager?.lockTimeoutMinutes ?: 0) }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.importUniversitiesFromCsv(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business & Agency Settings", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveBusinessSettings(
                                BusinessSettings(
                                    agencyName = agencyName,
                                    ceoName = ceoName,
                                    officialEmail = officialEmail,
                                    whatsappNumber = whatsappNumber,
                                    emailSignature = emailSignature,
                                    defaultTone = defaultTone,
                                    defaultCommissionModel = defaultCommission,
                                    website = website,
                                    isDarkTheme = isDarkTheme
                                )
                            )
                            securityManager?.let {
                                it.isAppLockEnabled = appLockEnabled
                                it.lockTimeoutMinutes = lockTimeoutMinutes
                            }
                            Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Settings", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Installed UI Plugins", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    com.example.core.plugin.PluginRegistry.getInstalledPlugins().forEach { plugin ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(plugin.name, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(plugin.version, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        if (plugin.description.isNotEmpty()) {
                            Text(plugin.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    }
                }
            }

            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Agency Profile", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = agencyName,
                        onValueChange = { agencyName = it },
                        label = { Text("Agency Name") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ceoName,
                        onValueChange = { ceoName = it },
                        label = { Text("CEO / Managing Director Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = officialEmail,
                        onValueChange = { officialEmail = it },
                        label = { Text("Official Agency Email (OAuth Verified)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("Agency WhatsApp Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text("Agency Website") },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = emailSignature,
                        onValueChange = { emailSignature = it },
                        label = { Text("Default Email Signature") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Biometric & App Lock Security Settings
            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFFD4AF37))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Biometric App Lock", style = MaterialTheme.typography.titleMedium)
                        }
                        Switch(
                            checked = appLockEnabled,
                            onCheckedChange = {
                                appLockEnabled = it
                                securityManager?.isAppLockEnabled = it
                            }
                        )
                    }

                    securityManager?.let { sm ->
                        Text(
                            text = sm.getBiometricStatusDescription(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (appLockEnabled) {
                        Text("Lock Timeout when in Background", style = MaterialTheme.typography.bodyMedium)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val options = listOf(0 to "Immediate", 1 to "1 Min", 5 to "5 Mins", 15 to "15 Mins")
                            options.forEach { (mins, label) ->
                                FilterChip(
                                    selected = lockTimeoutMinutes == mins,
                                    onClick = {
                                        lockTimeoutMinutes = mins
                                        securityManager?.lockTimeoutMinutes = mins
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                securityManager?.forceLock()
                                Toast.makeText(context, "App locked for testing", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF071D12))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Lock Screen Now", color = Color(0xFF071D12))
                        }
                    }
                }
            }

            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("AI & Outreach Defaults", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)

                    OutlinedTextField(
                        value = defaultTone,
                        onValueChange = { defaultTone = it },
                        label = { Text("Default Outreach Email Tone") },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = defaultCommission,
                        onValueChange = { defaultCommission = it },
                        label = { Text("Default B2B Commission Model Statement") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Glassmorphism Theme", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { isDarkTheme = it }
                        )
                    }
                }
            }

            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Data Management (CSV)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { csvLauncher.launch("text/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import CSV")
                        }

                        OutlinedButton(
                            onClick = {
                                val csvFile = CsvUtils.exportUniversitiesToCsv(context, universities)
                                if (csvFile != null) {
                                    IntentUtils.shareFile(context, csvFile, "Export University Contacts CSV")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV")
                        }
                    }
                }
            }

            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("System & Platform Version", style = MaterialTheme.typography.titleMedium, color = Color(0xFF00AEEF))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("App Version", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text("v4.2.0-AIOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Build Version", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text("2026.08.05.r1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("AIOS Engine Version", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text("Phase 7 Enterprise AIOS", color = Color(0xFF00AEEF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            GlassCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("About NextBorder AIOS", style = MaterialTheme.typography.titleMedium, color = Color(0xFFC0C0C0))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Founder & CEO", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text("Ishak Dawan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Company", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text("NextBorder Visa Consultancy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Text(
                        text = "Global Education • Visa Consultancy • Autonomous AI Swarm Intelligence",
                        color = Color(0xFFC0C0C0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
