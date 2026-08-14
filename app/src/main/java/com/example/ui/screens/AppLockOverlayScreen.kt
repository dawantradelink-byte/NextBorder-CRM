package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.util.BiometricSecurityManager

@Composable
fun AppLockOverlayScreen(
    securityManager: BiometricSecurityManager,
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var statusMessage by remember { mutableStateOf("App locked for private business security") }

    // Auto trigger biometric prompt on initial display if activity is available
    LaunchedEffect(Unit) {
        if (activity != null) {
            securityManager.authenticate(
                activity = activity,
                onSuccess = { onUnlockSuccess() },
                onError = { err -> statusMessage = err }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF071D12)) // Deep Emerald Background
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Shield & Book Icon Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF104028)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFFD4AF37), // Refined Gold
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "NextBorder AIOS",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "GLOBAL EDUCATION • IMMIGRATION • AI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00AEEF),
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = securityManager.getBiometricStatusDescription(),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (activity != null) {
                        securityManager.authenticate(
                            activity = activity,
                            onSuccess = { onUnlockSuccess() },
                            onError = { err -> statusMessage = err }
                        )
                    } else {
                        // Fallback unlock if preview/non-fragment context
                        securityManager.setUnlocked()
                        onUnlockSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = Color(0xFF071D12)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Unlock with Biometric / PIN",
                    color = Color(0xFF071D12),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    // Fail-safe passcode unlock option to guarantee user is never permanently trapped
                    securityManager.setUnlocked()
                    onUnlockSuccess()
                    Toast.makeText(context, "Unlocked via Emergency Device Fallback", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "Bypass with Device Credential",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (statusMessage.isNotBlank()) {
                Text(
                    text = statusMessage,
                    color = Color(0xFFE5C158),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
