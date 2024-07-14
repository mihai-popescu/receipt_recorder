package com.example.receipt.recorder

import com.example.receipt.recorder.document.scanner.DocumentScannerViewModel
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.model.camera.CameraData
import com.example.receipt.recorder.persistence.ReceiptRecorderDatabaseRoom
import com.example.receipt.recorder.persistence.ReceiptRecorderDatabaseRoomBuilder
import com.example.receipt.recorder.repository.ReceiptMapperFacade
import com.example.receipt.recorder.repository.ReceiptMapperFacadeFactory
import com.example.receipt.recorder.repository.ReceiptRepository
import com.example.receipt.recorder.repository.ReceiptRepositoryImpl
import com.example.receipt.recorder.ui.ReceiptDetailsViewModel
import com.example.receipt.recorder.ui.ReceiptListViewModel
import com.example.receipt.recorder.ui.camera.CameraConfirmationViewModel
import com.example.receipt.recorder.ui.camera.CameraViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ReceiptRecorderModule = module {
    single<ReceiptRecorderDatabaseRoom> {
        ReceiptRecorderDatabaseRoomBuilder.databaseBuilder(androidApplication()).build()
    }
    factory { ReceiptMapperFacadeFactory.createReceiptMapperFacade() }

    factory<ReceiptRepository> { ReceiptRepositoryImpl(get<ReceiptRecorderDatabaseRoom>(), get<ReceiptMapperFacade>()) }

    single<Camera> { CameraImpl() }

    viewModel { ReceiptListViewModel() }
    viewModel { (receipt: Receipt) -> ReceiptDetailsViewModel(receipt) }
    viewModel { CameraViewModel() }
    viewModel { (path: String) -> CameraConfirmationViewModel(path) }

    viewModel { DocumentScannerViewModel() }


    scope(named(CameraData.scopeName)) {
        scoped { CameraData() }
    }
}