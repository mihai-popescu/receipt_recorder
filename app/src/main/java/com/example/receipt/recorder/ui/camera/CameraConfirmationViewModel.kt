package com.example.receipt.recorder.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receipt.recorder.extension.rotate
import com.example.receipt.recorder.util.FileType
import com.example.receipt.recorder.util.getImageOrientation
import com.example.receipt.recorder.util.saveMediaFile
import kotlinx.coroutines.launch
import java.io.File

class CameraConfirmationViewModel(val path: String) : ViewModel() {
    fun getBitmap(path: String): Bitmap {
        return BitmapFactory.decodeFile(path).rotate(getImageOrientation(path).toFloat())
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