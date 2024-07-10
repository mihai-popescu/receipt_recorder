package com.example.receipt.recorder.ui

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receipt.recorder.Camera
import com.example.receipt.recorder.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReceiptListViewModel: ViewModel(), KoinComponent {

    val camera: Camera by inject()

    private val _confirmCapturedContent = MutableStateFlow(Event(Uri.EMPTY))
    val confirmCapturedContent = _confirmCapturedContent.asStateFlow()

    fun onMediaCaptureSuccess(context: Context, captureUri: Uri) {
        camera.clean()
        viewModelScope.launch(Dispatchers.IO) {
            convertMediaUriToContentUri(captureUri)?.let { uri ->
                MediaScannerConnection.scanFile(
                    context, arrayOf(uri.toString()), null
                ) { _, _ ->
                    captureUri.let { uri ->
                        viewModelScope.launch {
                            _confirmCapturedContent.emit(Event(uri))
                        }
                    }
                }
            }
        }
    }

    fun onMediaCaptureFailure() {
        camera.clean()
    }


    private fun convertMediaUriToContentUri(uri: Uri?): Uri? {
        return uri?.lastPathSegment?.toLongOrNull()?.let { id ->
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                    MediaStore.Files.getContentUri(MediaStore.getVolumeName(uri), id)
                else ->
                    MediaStore.Files.getContentUri(uri.pathSegments.first(), id)
            }
        }
    }

}