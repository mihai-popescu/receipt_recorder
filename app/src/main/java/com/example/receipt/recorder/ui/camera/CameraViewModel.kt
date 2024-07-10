package com.example.receipt.recorder.ui.camera

import android.os.Build
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.ViewModel
import com.example.receipt.recorder.model.AppPermission
import com.example.receipt.recorder.model.camera.CameraData
import com.example.receipt.recorder.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.net.URI
import java.util.UUID

class CameraViewModel: ViewModel() {
    companion object {

        private const val TAG = "CameraViewModel"
    }

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()

    private val _saveSuccessful = MutableStateFlow(Event(""))
    val saveSuccessful: StateFlow<Event<String>> = _saveSuccessful.asStateFlow()

    var outputFile: File? = null
        private set

    val cameraPermissionsNeeded = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> listOf(
            AppPermission.ReadExternalStorage,
            AppPermission.WriteExternalStorage,
            AppPermission.Camera)
        else -> listOf(
            AppPermission.AccessMediaLocation,
            AppPermission.ReadMediaImages,
            AppPermission.Camera)
    }

    private val cameraData = CameraData.getDefaultInstance()

    fun createOutputFile(): File {
        // delete old file
        deleteOutputFile()

        with(
            File.createTempFile(
                UUID.randomUUID().toString(),  ".jpg")) {
            outputFile = this
            return this
        }
    }



    private fun deleteOutputFile() {
        try {
            outputFile?.delete()
        } catch (_: Exception) {
        } finally {
            outputFile = null
        }
    }

    val onImageSavedCallback = object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
            stopPhotoCapture()
            // TODO bubble up error
            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            stopPhotoCapture()
            saveFile(outputFile)
        }
    }



    fun startPhotoCapture() {
        _isCapturing.value = true
    }

    fun stopPhotoCapture() {
        _isCapturing.value = false
    }

    fun saveFile(outputFile: File?) {
        outputFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                _saveSuccessful.value = Event(file.path)
            }
        }
    }

    fun onCaptureResultReceived(result: CameraFragment.CaptureResult) {
        cameraData.updateCaptureResult(result)
    }

    fun onCaptureCancelled(result: CameraFragment.CaptureCancel) {
        cameraData.updateCaptureCancelled(result)
    }
}