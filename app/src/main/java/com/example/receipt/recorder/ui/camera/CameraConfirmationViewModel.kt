package com.example.receipt.recorder.ui.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.load
import com.example.receipt.recorder.util.FileType
import com.example.receipt.recorder.util.saveMediaFile
import kotlinx.coroutines.launch
import java.io.File

class CameraConfirmationViewModel(private val path: String) : ViewModel() {
    fun loadImage(target: ImageView) {
        target.load(path)
    }

    fun usePhoto(context: Context, onCollect: (Uri?) -> Unit) {
        viewModelScope.launch {
            val path = "${Environment.DIRECTORY_PICTURES}/ReceiptRecorder"
            val tmpFile = File(this@CameraConfirmationViewModel.path)
            saveMediaFile(context, tmpFile, path, FileType.PHOTO).collect { savedMediaUri ->
                onCollect.invoke(savedMediaUri)
            }
        }
    }
}