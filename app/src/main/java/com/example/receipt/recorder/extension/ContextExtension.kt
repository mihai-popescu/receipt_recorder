package com.example.receipt.recorder.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.receipt.recorder.model.AppPermission

fun Context.isPermissionGranted(permission: AppPermission) = run {
    (ContextCompat.checkSelfPermission(this, permission.permissionName) == PackageManager.PERMISSION_GRANTED)
}