package com.example.receipt.recorder

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.receipt.recorder.model.camera.CameraData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface Camera {


    fun startMediaCapture(
        parent: Fragment,
        navDirections: NavDirections? = null,
        onMediaCaptureSuccess: ((Uri) -> Unit),
        onMediaCaptureFailure: (() -> Unit)
    )

    fun clean() = Unit
}

class CameraImpl: Camera, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

//    private var startMediaCaptureJob: Job? = null
    private var mediaCaptureSuccessJob: Job? = null
    private var mediaCaptureCancelledJob: Job? = null


    private var cameraData: CameraData? = null

    override fun startMediaCapture(
        parent: Fragment,
        navDirections: NavDirections?,
        onMediaCaptureSuccess: (Uri) -> Unit,
        onMediaCaptureFailure: () -> Unit,
        ) {
        observeMediaCaptureSuccess(onMediaCaptureSuccess)
        observeMediaCaptureCancelledResult(onMediaCaptureFailure)

        navDirections?.let {
            parent.findNavController().navigate(navDirections)
        }
    }

    private fun observeMediaCaptureSuccess(onMediaCaptureSuccess: (Uri) -> Unit) {
        mediaCaptureSuccessJob?.cancel()
        cameraData = CameraData.getDefaultInstance()
        mediaCaptureSuccessJob = launch {
            cameraData?.captureSuccess?.collect { resultData ->
                resultData?.let {
                    withContext(Dispatchers.Main) {
                        onMediaCaptureSuccess.invoke (it.uri)
                    }
                }
            }
        }.also {
            it.invokeOnCompletion {
                mediaCaptureSuccessJob = null
            }
        }
    }

    private fun observeMediaCaptureCancelledResult(onMediaCaptureFailure: () -> Unit) {
        mediaCaptureCancelledJob?.cancel()
        mediaCaptureCancelledJob = launch {
            cameraData?.captureCancelled?.collect { cancelledData ->
                cancelledData?.let {
                    withContext(Dispatchers.Main) {
                        onMediaCaptureFailure.invoke()
                    }
                }
            }
        }.also {
            it.invokeOnCompletion {
                mediaCaptureCancelledJob = null
            }
        }
    }

    override fun clean() {
        if (cameraData != null) CameraData.deleteDefaultInstance()
        cameraData = null
        mediaCaptureSuccessJob?.cancel()
        mediaCaptureCancelledJob?.cancel()
     }
}