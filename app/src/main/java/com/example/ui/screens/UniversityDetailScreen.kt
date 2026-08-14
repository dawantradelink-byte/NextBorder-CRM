package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessSettings
import com.example.data.University
import com.example.ui.components.GlassCard
import com.example.util.AiEmailGeneratorService
import com.example.util.IntentUtils
import com.example.util.PdfReportExporter
import com.example.util.ValidationUtils
import com.example.viewmodel.CrmViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityDetailScreen(
    uniId: Int?,
    viewModel: CrmViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allUnis by viewModel.allUniversities.collectAsState()
    val settings by viewModel.businessSettings.collectAsState()

    val existingUni = remember(allUnis, uniId) { allUnis.find { it.id == uniId } }

    var isEditMode by remember { mutableStateOf(uniId == null) }
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Profile, 1: Contact, 2: Terms, 3: Notes & History, 4: AI Draft

    // Form fields
    var name by remember(existingUni) { mutableStateOf(existingUni?.name ?: "") }
    var contactName by remember(existingUni) { mutableStateOf(existingUni?.contactName ?: "") }
    var email by remember(existingUni) { mutableStateOf(existingUni?.email ?: "") }
    var country by remember(existingUni) { mutableStateOf(existingUni?.country ?: "United Kingdom") }
    var city by remember(existingUni) { mutableStateOf(existingUni?.city ?: "") }
    var website by remember(existingUni) { mutableStateOf(existingUni?.website ?: "") }
    var whatsappNumber by remember(existingUni) { mutableStateOf(existingUni?.whatsappNumber ?: "+447700900077") }
    var partnershipStatus by remember(existingUni) { mutableStateOf(existingUni?.partnershipStatus ?: "Prospect") }
    var priority by remember(existingUni) { mutableStateOf(existingUni?.priority ?: "Medium") }
    var intakeMonths by remember(existingUni) { mutableStateOf(existingUni?.intakeMonths ?: "Jan, Sep") }
    var commissionRate by remember(existingUni) { mutableStateOf(existingUni?.commissionRate ?: "15%") }
    var applicationFee by remember(existingUni) { mutableStateOf(existingUni?.applicationFee ?: "Free") }
    var tuitionFees by remember(existingUni) { mutableStateOf(existingUni?.tuitionFees ?: "£13,500/year") }
    var tags by remember(existingUni) { mutableStateOf(existingUni?.tags ?: "Middle-Class, Easy Admission") }
    var assignedCounselor by remember(existingUni) { mutableStateOf(existingUni?.assignedCounselor ?: settings.ceoName) }
    var notesText by remember(existingUni) { mutableStateOf(existingUni?.notes ?: "") }

    var acceptsAgents by remember(existingUni) { mutableStateOf(existingUni?.acceptsAgents ?: true) }
    var moiAccepted by remember(existingUni) { mutableStateOf(existingUni?.moiAccepted ?: true) }
    var bbaAvailable by remember(existingUni) { mutableStateOf(existingUni?.bbaAvailable ?: true) }
    var mbaAvailable by remember(existingUni) { mutableStateOf(existingUni?.mbaAvailable ?: true) }
    var scholarshipsAvailable by remember(existingUni) { mutableStateOf(existingUni?.scholarshipsAvailable ?: true) }

    // Validation state
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    // AI Email state
    var selectedTone by remember { mutableStateOf(settings.defaultTone) }
    var selectedTemplate by remember { mutableStateOf("First Outreach") }
    var generatedSubject by remember { mutableStateOf("") }
    var generatedBody by remember { mutableStateOf("") }
    var isGeneratingAi by remember { mutableStateOf(false) }

    // Dialogs
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    fun validateAndSave() {
        nameError = if (name.trim().isBlank()) "University name is required" else null
        emailError = if (email.isNotBlank() && !ValidationUtils.isValidEmail(email)) "Invalid email format" else null

        if (nameError != null || emailError != null) {
            Toast.makeText(context, nameError ?: emailError ?: "Validation failed", Toast.LENGTH_SHORT).show()
            return
        }

        val uniToSave = University(
            id = existingUni?.id ?: 0,
            name = name,
            contactName = contactName,
            email = email,
            country = country,
            city = city,
            website = website,
            whatsappNumber = whatsappNumber,
            partnershipStatus = partnershipStatus,
            priority = priority,
            intakeMonths = intakeMonths,
            commissionRate = commissionRate,
            applicationFee = applicationFee,
            tuitionFees = tuitionFees,
            tags = tags,
            assignedCounselor = assignedCounselor,
            notes = notesText,
            acceptsAgents = acceptsAgents,
            moiAccepted = moiAccepted,
            bbaAvailable = bbaAvailable,
            mbaAvailable = mbaAvailable,
            scholarshipsAvailable = scholarshipsAvailable
        )

        if (existingUni == null) {
            viewModel.addUniversity(uniToSave) { onBack() }
        } else {
            viewModel.updateUniversity(uniToSave) { isEditMode = false }
        }
    }

    if (showDeleteConfirm && existingUni != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete University Partner?") },
            text = { Text("Are you sure you want to permanently delete '${existingUni.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUniversityPermanently(existingUni)
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete Permanently") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (existingUni == null) "Add University Partner" else existingUni.name,
                        color = Color.White,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (existingUni != null) {
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(
                                if (isEditMode) Icons.Default.Visibility else Icons.Default.Edit,
                                contentDescription = "Toggle Edit Mode",
                                tint = Color(0xFFFF8A00)
                            )
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Action Buttons Row (WhatsApp, Email, Calendar, Browser, PDF, Greylist, Blacklist)
            if (existingUni != null) {
                var showGreylistDialog by remember { mutableStateOf(false) }
                var showBlacklistDialog by remember { mutableStateOf(false) }
                var refusalText by remember { mutableStateOf("") }
                var refusalReason by remember { mutableStateOf("") }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuickActionButton("Email", Icons.Default.Email, Color(0xFFFF8A00)) {
                                if (partnershipStatus == "BLACKLIST") {
                                    Toast.makeText(context, "Blocked: University is Blacklisted!", Toast.LENGTH_SHORT).show()
                                } else {
                                    IntentUtils.launchEmail(context, email, "Student Recruitment Partnership: ${settings.agencyName} x ${existingUni.name}", "")
                                }
                            }
                            QuickActionButton("WhatsApp", Icons.AutoMirrored.Filled.Chat, Color(0xFF10B981)) {
                                if (partnershipStatus == "BLACKLIST") {
                                    Toast.makeText(context, "Blocked: University is Blacklisted!", Toast.LENGTH_SHORT).show()
                                } else {
                                    IntentUtils.launchWhatsApp(context, whatsappNumber, "Hi ${existingUni.contactName}, writing from ${settings.agencyName}...")
                                }
                            }
                            QuickActionButton("Calendar", Icons.Default.CalendarToday, Color(0xFFFF2E93)) {
                                IntentUtils.launchGoogleCalendar(context, "Call with ${existingUni.name}", "Discuss student intake partnership")
                            }
                            QuickActionButton("Browser", Icons.Default.Language, Color(0xFF3B82F6)) {
                                IntentUtils.launchWebBrowser(context, website.ifBlank { "https://google.com" })
                            }
                            QuickActionButton("Export PDF", Icons.Default.PictureAsPdf, Color(0xFF8B5CF6)) {
                                PdfReportExporter.exportUniversitySummaryPdf(context, existingUni)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { showGreylistDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Greylist (Temp)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showBlacklistDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Blacklist (Stop)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Greylist Dialog
                if (showGreylistDialog) {
                    AlertDialog(
                        onDismissRequest = { showGreylistDialog = false },
                        containerColor = Color(0xFF1F2937),
                        title = { Text("Move to Greylist (Temporary Refusal)", color = Color.White) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Reason for refusal / response snippet:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                OutlinedTextField(
                                    value = refusalText,
                                    onValueChange = { refusalText = it },
                                    placeholder = { Text("e.g. 'We are not accepting new agents for this cycle.'") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = transparentTextFieldColors()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.moveUniversityToGreylist(existingUni.id, conversationText = refusalText)
                                    showGreylistDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                            ) { Text("Confirm Greylist") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showGreylistDialog = false }) { Text("Cancel", color = Color.White) }
                        }
                    )
                }

                // Blacklist Dialog
                if (showBlacklistDialog) {
                    AlertDialog(
                        onDismissRequest = { showBlacklistDialog = false },
                        containerColor = Color(0xFF1F2937),
                        title = { Text("Move to Blacklist (Permanent Block)", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Warning: Blacklisting will permanently block future email outreach and remove follow-up alarms for this university.", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                OutlinedTextField(
                                    value = refusalReason,
                                    onValueChange = { refusalReason = it },
                                    placeholder = { Text("e.g. 'Never contact us again / Legal notice'") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = transparentTextFieldColors()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.moveUniversityToBlacklist(existingUni.id, conversationText = refusalReason)
                                    showBlacklistDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) { Text("Permanently Blacklist") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showBlacklistDialog = false }) { Text("Cancel", color = Color.White) }
                        }
                    )
                }

                // Greylist / Blacklist Status Notice Banner
                if (existingUni.partnershipStatus == "GREYLIST") {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GREYLISTED — TEMPORARY REJECTION", color = Color(0xFFF59E0B), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                            Text("Reason: ${existingUni.greylistReason.ifBlank { "Temporary disinterest / capacity limit" }}", color = Color.White, fontSize = 12.sp)
                            Text("Refusal Date: ${if (existingUni.greylistRefusalDate > 0) java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(existingUni.greylistRefusalDate)) else "Recent"}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("Suggested Follow-up Date: ${if (existingUni.greylistSuggestedFollowUpDate > 0) java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(existingUni.greylistSuggestedFollowUpDate)) else "In 6 Months"}", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Recommended Strategy: ${existingUni.greylistRecommendedStrategy.ifBlank { "Do not spam; re-pitch with updated CEO profile and student volume metrics." }}", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
                        }
                    }
                } else if (existingUni.partnershipStatus == "BLACKLIST") {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("BLACKLISTED — PERMANENT REFUSAL", color = Color(0xFFEF4444), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                            Text("Reason: ${existingUni.blacklistReason.ifBlank { "Explicit refusal or legal policy request" }}", color = Color.White, fontSize = 12.sp)
                            Text("All future outreach, automated emails, and follow-up alarms are strictly prohibited.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Section Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFF8A00),
                edgePadding = 0.dp
            ) {
                listOf("Profile", "Contact & Links", "Terms & Criteria", "Notes", "AI Email Draft").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedSection == idx,
                        onClick = { selectedSection = idx },
                        text = { Text(title, fontWeight = if (selectedSection == idx) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Section Content
            when (selectedSection) {
                0 -> ProfileSectionCard(
                    isEditMode = isEditMode,
                    name = name, onNameChange = { name = it }, nameError = nameError,
                    country = country, onCountryChange = { country = it },
                    city = city, onCityChange = { city = it },
                    partnershipStatus = partnershipStatus, onPartnershipStatusChange = { partnershipStatus = it },
                    priority = priority, onPriorityChange = { priority = it },
                    assignedCounselor = assignedCounselor, onAssignedCounselorChange = { assignedCounselor = it },
                    tags = tags, onTagsChange = { tags = it }
                )
                1 -> ContactSectionCard(
                    isEditMode = isEditMode,
                    contactName = contactName, onContactNameChange = { contactName = it },
                    email = email, onEmailChange = { email = it }, emailError = emailError,
                    whatsappNumber = whatsappNumber, onWhatsAppChange = { whatsappNumber = it },
                    website = website, onWebsiteChange = { website = it },
                    onCopyEmail = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Email", email))
                        Toast.makeText(context, "Email copied", Toast.LENGTH_SHORT).show()
                    }
                )
                2 -> TermsSectionCard(
                    isEditMode = isEditMode,
                    intakeMonths = intakeMonths, onIntakeChange = { intakeMonths = it },
                    commissionRate = commissionRate, onCommissionChange = { commissionRate = it },
                    applicationFee = applicationFee, onFeeChange = { applicationFee = it },
                    tuitionFees = tuitionFees, onTuitionChange = { tuitionFees = it },
                    acceptsAgents = acceptsAgents, onAcceptsAgentsChange = { acceptsAgents = it },
                    moiAccepted = moiAccepted, onMoiAcceptedChange = { moiAccepted = it },
                    bbaAvailable = bbaAvailable, onBbaChange = { bbaAvailable = it },
                    mbaAvailable = mbaAvailable, onMbaChange = { mbaAvailable = it },
                    scholarshipsAvailable = scholarshipsAvailable, onScholarshipsChange = { scholarshipsAvailable = it }
                )
                3 -> NotesSectionCard(
                    isEditMode = isEditMode,
                    notesText = notesText,
                    onNotesChange = { notesText = it },
                    viewModel = viewModel,
                    uniId = existingUni?.id
                )
                4 -> AiDraftComposerCard(
                    uni = existingUni ?: University(name = name, contactName = contactName, email = email, country = country),
                    settings = settings,
                    selectedTone = selectedTone, onToneChange = { selectedTone = it },
                    selectedTemplate = selectedTemplate, onTemplateChange = { selectedTemplate = it },
                    generatedSubject = generatedSubject, onSubjectChange = { generatedSubject = it },
                    generatedBody = generatedBody, onBodyChange = { generatedBody = it },
                    isGenerating = isGeneratingAi,
                    onGenerate = { extra ->
                        scope.launch {
                            isGeneratingAi = true
                            val result = AiEmailGeneratorService.generateOutreachEmail(
                                university = existingUni ?: University(name = name, contactName = contactName, email = email),
                                templateType = selectedTemplate,
                                tone = selectedTone,
                                settings = settings,
                                extraInstruction = extra
                            )
                            generatedSubject = result.subject
                            generatedBody = result.body
                            isGeneratingAi = false
                        }
                    },
                    onCopyDraft = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Draft", "$generatedSubject\n\n$generatedBody"))
                        Toast.makeText(context, "Email draft copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onSendEmail = {
                        IntentUtils.launchEmail(context, email, generatedSubject, generatedBody)
                    }
                )
            }

            if (isEditMode) {
                Button(
                    onClick = { validateAndSave() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (existingUni == null) "Create University Partner" else "Save University Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.2f),
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

@Composable
fun ProfileSectionCard(
    isEditMode: Boolean,
    name: String, onNameChange: (String) -> Unit, nameError: String?,
    country: String, onCountryChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    partnershipStatus: String, onPartnershipStatusChange: (String) -> Unit,
    priority: String, onPriorityChange: (String) -> Unit,
    assignedCounselor: String, onAssignedCounselorChange: (String) -> Unit,
    tags: String, onTagsChange: (String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("University Profile", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF8A00))

            if (isEditMode) {
                OutlinedTextField(
                    value = name, onValueChange = onNameChange,
                    label = { Text("University Name *") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = country, onValueChange = onCountryChange,
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = city, onValueChange = onCityChange,
                    label = { Text("City / Region") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = partnershipStatus, onValueChange = onPartnershipStatusChange,
                    label = { Text("Status (Prospect, Contacted, In Discussion, Partnered, On Hold)") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = priority, onValueChange = onPriorityChange,
                    label = { Text("Priority Level (High, Medium, Low)") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = assignedCounselor, onValueChange = onAssignedCounselorChange,
                    label = { Text("Assigned Counselor") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = tags, onValueChange = onTagsChange,
                    label = { Text("Tags / Categories") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )
            } else {
                DetailRow("University Name", name)
                DetailRow("Location", "$country, $city")
                DetailRow("Partnership Status", partnershipStatus)
                DetailRow("Priority Level", priority)
                DetailRow("Counselor", assignedCounselor)
                DetailRow("Tags", tags)
            }
        }
    }
}

@Composable
fun ContactSectionCard(
    isEditMode: Boolean,
    contactName: String, onContactNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit, emailError: String?,
    whatsappNumber: String, onWhatsAppChange: (String) -> Unit,
    website: String, onWebsiteChange: (String) -> Unit,
    onCopyEmail: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Contact & Online Identity", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF8A00))

            if (isEditMode) {
                OutlinedTextField(
                    value = contactName, onValueChange = onContactNameChange,
                    label = { Text("Partnership Manager / Contact Name") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = email, onValueChange = onEmailChange,
                    label = { Text("Partnership Email") },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = whatsappNumber, onValueChange = onWhatsAppChange,
                    label = { Text("WhatsApp Phone Number") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = website, onValueChange = onWebsiteChange,
                    label = { Text("Official Website URL") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )
            } else {
                DetailRow("Contact Person", contactName)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DetailRow("Email", email)
                    if (email.isNotBlank()) {
                        TextButton(onClick = onCopyEmail) { Text("Copy", color = Color(0xFFFF8A00)) }
                    }
                }
                DetailRow("WhatsApp", whatsappNumber)
                DetailRow("Website", website)
            }
        }
    }
}

@Composable
fun TermsSectionCard(
    isEditMode: Boolean,
    intakeMonths: String, onIntakeChange: (String) -> Unit,
    commissionRate: String, onCommissionChange: (String) -> Unit,
    applicationFee: String, onFeeChange: (String) -> Unit,
    tuitionFees: String, onTuitionChange: (String) -> Unit,
    acceptsAgents: Boolean, onAcceptsAgentsChange: (Boolean) -> Unit,
    moiAccepted: Boolean, onMoiAcceptedChange: (Boolean) -> Unit,
    bbaAvailable: Boolean, onBbaChange: (Boolean) -> Unit,
    mbaAvailable: Boolean, onMbaChange: (Boolean) -> Unit,
    scholarshipsAvailable: Boolean, onScholarshipsChange: (Boolean) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Recruitment Terms & Criteria", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF8A00))

            if (isEditMode) {
                OutlinedTextField(
                    value = intakeMonths, onValueChange = onIntakeChange,
                    label = { Text("Intake Months (e.g. Jan, Sep)") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = commissionRate, onValueChange = onCommissionChange,
                    label = { Text("Commission Rate (%)") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = applicationFee, onValueChange = onFeeChange,
                    label = { Text("Application Fee") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = tuitionFees, onValueChange = onTuitionChange,
                    label = { Text("Average Tuition Fees") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                Text("Criteria Checklist", fontWeight = FontWeight.Bold, color = Color.White)
                CheckboxRow("Accepts B2B Agents", acceptsAgents, onAcceptsAgentsChange)
                CheckboxRow("Medium of Instruction (MOI) Accepted", moiAccepted, onMoiAcceptedChange)
                CheckboxRow("BBA Programs Available", bbaAvailable, onBbaChange)
                CheckboxRow("MBA Programs Available", mbaAvailable, onMbaChange)
                CheckboxRow("Scholarships Available", scholarshipsAvailable, onScholarshipsChange)
            } else {
                DetailRow("Intake Months", intakeMonths)
                DetailRow("Commission Rate", commissionRate)
                DetailRow("Application Fee", applicationFee)
                DetailRow("Tuition Fees", tuitionFees)
                DetailRow("Accepts Agents", if (acceptsAgents) "Yes" else "No")
                DetailRow("MOI Accepted", if (moiAccepted) "Yes" else "No")
                DetailRow("BBA / MBA", "${if (bbaAvailable) "BBA " else ""}${if (mbaAvailable) "MBA" else ""}".ifBlank { "N/A" })
                DetailRow("Scholarships", if (scholarshipsAvailable) "Yes" else "No")
            }
        }
    }
}

@Composable
fun NotesSectionCard(
    isEditMode: Boolean,
    notesText: String, onNotesChange: (String) -> Unit,
    viewModel: CrmViewModel,
    uniId: Int?
) {
    val notesList by viewModel.selectedNotes.collectAsState()
    val contactLogs by viewModel.selectedContactLogs.collectAsState()
    var newLogText by remember { mutableStateOf("") }
    var logType by remember { mutableStateOf("Email") }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Notes & Outreach History", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF8A00))

            if (isEditMode) {
                OutlinedTextField(
                    value = notesText, onValueChange = onNotesChange,
                    label = { Text("General Notes & Admission Policies") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )
            } else {
                Text(notesText.ifBlank { "No notes recorded." }, color = Color.White.copy(alpha = 0.9f))
            }

            if (uniId != null) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Text("Log Activity / Contact", fontWeight = FontWeight.Bold, color = Color.White)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Email", "Call", "WhatsApp", "Meeting").forEach { t ->
                        FilterChip(
                            selected = logType == t,
                            onClick = { logType = t },
                            label = { Text(t) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF8A00),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newLogText, onValueChange = { newLogText = it },
                        placeholder = { Text("Log outcome/summary...") },
                        modifier = Modifier.weight(1f), singleLine = true,
                        colors = transparentTextFieldColors()
                    )
                    Button(
                        onClick = {
                            viewModel.addContactLog(uniId, logType, newLogText)
                            newLogText = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93))
                    ) {
                        Text("Log")
                    }
                }

                if (contactLogs.isNotEmpty()) {
                    Text("History Logs", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        contactLogs.take(5).forEach { log ->
                            Surface(color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("[${log.type}] ${log.summary}", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Follow-Up Alarm Notification", fontWeight = FontWeight.Bold, color = Color(0xFFFF2E93))
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFFF2E93))
                }

                val context = LocalContext.current
                val selectedUni = viewModel.selectedUniversity.collectAsState().value
                var reminderNote by remember { mutableStateOf("") }
                var selectedTimeOption by remember { mutableIntStateOf(1) } // 0: 10 Sec Test, 1: Tomorrow, 2: 3 Days, 3: 1 Week

                OutlinedTextField(
                    value = reminderNote,
                    onValueChange = { reminderNote = it },
                    placeholder = { Text("e.g. Verify partnership contract with admissions director") },
                    label = { Text("Follow-up Message / Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = transparentTextFieldColors()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val options = listOf("Test (10s)", "Tomorrow", "In 3 Days", "In 1 Week")
                    options.forEachIndexed { idx, opt ->
                        FilterChip(
                            selected = selectedTimeOption == idx,
                            onClick = { selectedTimeOption = idx },
                            label = { Text(opt, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF2E93),
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        val now = System.currentTimeMillis()
                        val triggerTimeMs = when (selectedTimeOption) {
                            0 -> now + 10_000L // 10 sec test alarm
                            1 -> now + 86_400_000L // Tomorrow
                            2 -> now + (3 * 86_400_000L) // 3 days
                            else -> now + (7 * 86_400_000L) // 1 week
                        }
                        val name = selectedUni?.name ?: "University Partner"
                        viewModel.scheduleFollowUpReminderWithNotification(
                            context = context,
                            universityId = uniId,
                            universityName = name,
                            reminderTimeMs = triggerTimeMs,
                            noteMessage = reminderNote.ifBlank { "CRM follow-up scheduled for $name" }
                        )
                        reminderNote = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Schedule Android System Alarm Notification", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun AiDraftComposerCard(
    uni: University,
    settings: BusinessSettings,
    selectedTone: String, onToneChange: (String) -> Unit,
    selectedTemplate: String, onTemplateChange: (String) -> Unit,
    generatedSubject: String, onSubjectChange: (String) -> Unit,
    generatedBody: String, onBodyChange: (String) -> Unit,
    isGenerating: Boolean,
    onGenerate: (String) -> Unit,
    onCopyDraft: () -> Unit,
    onSendEmail: () -> Unit
) {
    var extraInstruction by remember { mutableStateOf("") }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("AI Outreach Email Composer", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF8A00))

            Text("Select Template:", style = MaterialTheme.typography.labelMedium, color = Color.White)
            ScrollableTabRow(
                selectedTabIndex = 0,
                containerColor = Color.Transparent,
                edgePadding = 0.dp
            ) {
                listOf("First Outreach", "Follow-up 1", "Meeting Request", "Partnership Proposal", "Thank You").forEach { tmpl ->
                    FilterChip(
                        selected = selectedTemplate == tmpl,
                        onClick = { onTemplateChange(tmpl) },
                        label = { Text(tmpl, fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 6.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF2E93),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Text("Select Tone:", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Persuasive", "Friendly", "Professional", "Concise", "Premium").forEach { tone ->
                    FilterChip(
                        selected = selectedTone == tone,
                        onClick = { onToneChange(tone) },
                        label = { Text(tone, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF8A00),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = extraInstruction, onValueChange = { extraInstruction = it },
                placeholder = { Text("Extra instructions (e.g. mention MOI, scholarship)...") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                colors = transparentTextFieldColors()
            )

            Button(
                onClick = { onGenerate(extraInstruction) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E93))
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate / Regenerate Email Draft")
                }
            }

            if (generatedSubject.isNotBlank() || generatedBody.isNotBlank()) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                OutlinedTextField(
                    value = generatedSubject, onValueChange = onSubjectChange,
                    label = { Text("Subject Line") },
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                OutlinedTextField(
                    value = generatedBody, onValueChange = onBodyChange,
                    label = { Text("Email Body") },
                    minLines = 8,
                    modifier = Modifier.fillMaxWidth(), colors = transparentTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCopyDraft,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Draft")
                    }

                    Button(
                        onClick = onSendEmail,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A00))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send via Email")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}

@Composable
fun CheckboxRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFFF2E93),
                uncheckedColor = Color.White.copy(alpha = 0.5f),
                checkmarkColor = Color.White
            )
        )
        Text(text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun transparentTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFFFF2E93),
    focusedBorderColor = Color(0xFFFF2E93),
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    focusedLabelColor = Color(0xFFFF2E93),
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
)
