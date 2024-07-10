package com.example.receipt.recorder.model.camera

import com.example.receipt.recorder.ui.camera.CameraFragment
import kotlinx.coroutines.flow.*
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin


class CameraData {

	companion object {

		const val scopeName: String = "CameraData"
		private const val scopeId: String = "CameraDataScopeId"

		fun getDefaultInstance(): CameraData {
			return getKoin()
				.getOrCreateScope(scopeId, named(scopeName)).get()
		}

		fun deleteDefaultInstance() {
			return getKoin().deleteScope(scopeId)
		}
	}

	private val _captureSuccess = MutableStateFlow<CameraFragment.CaptureResult?>(null)
	val captureSuccess = _captureSuccess.asStateFlow()

	private val _captureCancelled = MutableStateFlow<CameraFragment.CaptureCancel?>(null)
	val captureCancelled = _captureCancelled.asStateFlow()

	fun updateCaptureResult(result: CameraFragment.CaptureResult) {
		_captureSuccess.value = result
	}

	fun updateCaptureCancelled(result: CameraFragment.CaptureCancel) {
		_captureCancelled.value = result
	}
}



