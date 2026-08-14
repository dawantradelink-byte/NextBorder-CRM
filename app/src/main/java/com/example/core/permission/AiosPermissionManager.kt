package com.example.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class AiosPermission {
    INTERNET,
    POST_NOTIFICATIONS,
    SCHEDULE_EXACT_ALARM,
    RECEIVE_BOOT_COMPLETED,
    BIOMETRIC,
    FOREGROUND_SERVICE
}

object AiosPermissionManager {

    fun isPermissionGranted(context: Context, permission: AiosPermission): Boolean {
        return when (permission) {
            AiosPermission.INTERNET -> true
            AiosPermission.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else true
            }
            AiosPermission.SCHEDULE_EXACT_ALARM -> true
            AiosPermission.RECEIVE_BOOT_COMPLETED -> true
            AiosPermission.BIOMETRIC -> true
            AiosPermission.FOREGROUND_SERVICE -> true
        }
    }

    fun getRequiredPermissionsList(): List<String> {
        val list = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return list
    }
}
