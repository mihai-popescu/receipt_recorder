package com.example.receipt.recorder

import com.example.receipt.recorder.model.camera.CameraData
import com.example.receipt.recorder.ui.ReceiptListViewModel
import com.example.receipt.recorder.ui.camera.CameraConfirmationViewModel
import com.example.receipt.recorder.ui.camera.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ReceiptRecorderModule = module {
    single<Camera> { CameraImpl() }

    viewModel { ReceiptListViewModel() }
    viewModel { CameraViewModel() }
    viewModel { (path: String) -> CameraConfirmationViewModel(path) }



    scope(named(CameraData.scopeName)) {
        scoped { CameraData() }
    }
}