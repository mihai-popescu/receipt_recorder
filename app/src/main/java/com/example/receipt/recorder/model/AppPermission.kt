package com.example.receipt.recorder.model

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class AppPermission(
	val permissionName: String
) {
	// On devices that run Android 10 or higher, you don't need any storage-related permissions
	// to access and modify media files that your app owns
	data object ReadExternalStorage :
		AppPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

	data object WriteExternalStorage :
		AppPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

	data object Camera :
		AppPermission(Manifest.permission.CAMERA)

	@RequiresApi(Build.VERSION_CODES.Q)
	data object AccessMediaLocation :
		AppPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)

	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	data object ReadMediaImages :
		AppPermission(Manifest.permission.READ_MEDIA_IMAGES)

}