package com.example.receipt.recorder.extension

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.receipt.recorder.model.AppPermission
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


fun Fragment.checkPermissions(
    permissions: List<AppPermission>,
    resultLauncher: ActivityResultLauncher<Array<String>>,
    onGranted: (List<AppPermission>) -> Unit,
    onRationaleNeeded: (List<AppPermission>) -> Unit
) {
    val noPermission = permissions.filter { !isPermissionGranted(it) }
    when {
        noPermission.isEmpty() -> onGranted(permissions)
        else -> {
            val neededPermissions = noPermission.filter { !shouldShowRationale(it) }
            when {
                neededPermissions.isEmpty() -> onRationaleNeeded(noPermission)
                else -> {
                    requestPermissions(permissions, resultLauncher)
                }
            }
        }
    }
}



private fun requestPermissions(
    permissions: List<AppPermission>,
    resultLauncher: ActivityResultLauncher<Array<String>>
) {
    if (permissions.isNotEmpty()) {
        resultLauncher.launch(permissions.map { it.permissionName }.toTypedArray())
    }
}

fun Fragment.preparePermissionsRequest(
    permissions: List<AppPermission>,
    onGranted: (List<AppPermission>) -> Unit,
    onDenied: (List<AppPermission>) -> Unit,
    onRationaleNeeded: (List<AppPermission>) -> Unit
) = when {
    permissions.isNotEmpty() -> registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
        handlePermissionResults(
            permissions,
            onGranted = { onGranted(it) },
            onRationaleNeeded = { onRationaleNeeded(it) },
            onDenied = { onDenied(it) })
    }
    else -> null
}

fun Fragment.handlePermissionResults(
    permissions: List<AppPermission>,
    onGranted: (List<AppPermission>) -> Unit,
    onDenied: (List<AppPermission>) -> Unit,
    onRationaleNeeded: (List<AppPermission>) -> Unit
) {
    val noPermission = permissions.filter { !isPermissionGranted(it) }
    when {
        noPermission.isEmpty() -> onGranted(permissions)
        else -> {
            val neededPermissions = noPermission.filter { !shouldShowRationale(it) }
            when {
                neededPermissions.isEmpty() -> onRationaleNeeded(noPermission)
                else -> {
                    onDenied(neededPermissions)
                }
            }
        }
    }
}

fun Fragment.isPermissionGranted(permission: AppPermission) = run {
    requireContext().isPermissionGranted(permission)
}

fun Fragment.shouldShowRationale(permission: AppPermission) = run {
    shouldShowRequestPermissionRationale(permission.permissionName)
}

fun <T> Fragment.stateFlowCollect(
    stateFlow: StateFlow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state) {
            stateFlow.collect(block)
        }
    }
}
