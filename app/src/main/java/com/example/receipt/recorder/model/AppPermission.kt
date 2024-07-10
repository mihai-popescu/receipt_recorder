package com.example.receipt.recorder.model

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class AppPermission(
	val permissionName: String, val requestCode: Int
) {

	companion object {

		val permissions: List<AppPermission> by lazy {
			listOf(Camera)
		}
	}

	// On devices that run Android 10 or higher, you don't need any storage-related permissions
	// to access and modify media files that your app owns
	object ReadExternalStorage :
		AppPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 51)

	object WriteExternalStorage :
		AppPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 52)

	object Camera :
		AppPermission(Manifest.permission.CAMERA, 53)

	object RecordAudio :
		AppPermission(Manifest.permission.RECORD_AUDIO, 54)

	@RequiresApi(Build.VERSION_CODES.Q)
	object AccessMediaLocation :
		AppPermission(Manifest.permission.ACCESS_MEDIA_LOCATION, 55)

	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	object ReadMediaImages :
		AppPermission(Manifest.permission.READ_MEDIA_IMAGES, 56)

	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	object ReadMediaVideo :
		AppPermission(Manifest.permission.READ_MEDIA_VIDEO, 57)

	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	object ReadMediaAudio :
		AppPermission(Manifest.permission.READ_MEDIA_AUDIO, 58)

}