package com.example.receipt.recorder.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class FileType(val mimeType: String) {
	VIDEO("video/mp4"),
	PHOTO("image/jpeg"),
	AUDIO("audio/m4a")
}

suspend fun saveMediaFile(context: Context, file: File, path: String, fileType: FileType, preferredName: String? = null): Flow<Uri?> {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
		saveFileInQ(file, path, context.contentResolver, fileType, preferredName)
	} else {
		legacyFileSave(file, path, context, preferredName)
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFileInQ(file: File, path: String, contentResolver: ContentResolver, fileType: FileType, preferredName: String?): Flow<Uri?> {
	return flow {
		val contentValues = ContentValues().apply {
			put(MediaStore.Video.Media.DISPLAY_NAME, preferredName ?: file.name)
			put(MediaStore.Video.Media.MIME_TYPE, fileType.mimeType)
			put(MediaStore.Video.Media.RELATIVE_PATH, path)
			put(MediaStore.Video.Media.IS_PENDING, 1)
		}

		val mediaStoreFileTypeCategoryUri = when (fileType) {
			FileType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
			FileType.PHOTO -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			FileType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		}

		val uri = contentResolver.insert(mediaStoreFileTypeCategoryUri, contentValues)
		uri?.let { contentResolver.openOutputStream(it) }.also { outputStream ->
			outputStream?.use {
				val inputStream = FileInputStream(file)
				val buffer = ByteArray(4096)
				while (true) {
					val bytesRead: Int = inputStream.read(buffer)
					if (bytesRead == -1) {
						break
					}
					it.write(buffer, 0, bytesRead)
				}
				it.flush()
			}
		}
		contentValues.apply {
			clear()
			put(MediaStore.Video.Media.IS_PENDING, 0)
		}
		uri?.let {
			contentResolver.update(it, contentValues, null, null)
		}
		emit(uri)
	}.flowOn(Dispatchers.IO)
}

@Suppress("DEPRECATION")
private suspend fun legacyFileSave(file: File, path: String, context: Context, preferredName: String?): Flow<Uri?> {
	return flow {
		val directory = Environment.getExternalStoragePublicDirectory(path)
		var isDirectoryCreated = true
		if (directory?.exists() == false) {
			isDirectoryCreated = directory.mkdirs()
		}
		if (!isDirectoryCreated) {
			emit(null)
		}
		val newFile = File(directory, preferredName ?: file.name)
		try {
			val buffer = ByteArray(4096)
			FileInputStream(file).use { inputStream ->
				FileOutputStream(newFile).use { outputStream ->
					while (true) {
						val read: Int = inputStream.read(buffer)
						if (read == -1) {
							break
						}
						outputStream.write(buffer, 0, read)
					}
					outputStream.flush()
					true
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
			emit(null)
		}

		emit(suspendCoroutine<Uri> { continuation ->
			MediaScannerConnection.scanFile(
				context, arrayOf(newFile.absolutePath), null
			) { _, uri ->
				continuation.resume(uri)
			}
		})
	}.flowOn(Dispatchers.IO)
}
