package com.example.receipt.recorder.document.scanner

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receipt.recorder.model.camera.CameraData
import com.example.receipt.recorder.ui.camera.CameraFragment
import com.example.receipt.recorder.util.FileType
import com.example.receipt.recorder.util.saveMediaFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DocumentScannerViewModel : ViewModel() {
    private val cameraData = CameraData.getDefaultInstance()

    fun onCaptureResultReceived(result: CameraFragment.CaptureResult) {
        cameraData.updateCaptureResult(result)
    }

    fun onCaptureCancelled(result: CameraFragment.CaptureCancel) {
        cameraData.updateCaptureCancelled(result)
    }

    fun usePhoto(context: Context, cacheFile: File, onProcessComplete: (Uri?) -> Unit) {
        viewModelScope.launch {
            val path = "${Environment.DIRECTORY_PICTURES}/ReceiptRecorder"
            saveMediaFile(
                context,
                cacheFile,
                path,
                FileType.PHOTO
            ).collect { savedMediaUri ->
                onProcessComplete.invoke(savedMediaUri)
            }

        }

    }
}