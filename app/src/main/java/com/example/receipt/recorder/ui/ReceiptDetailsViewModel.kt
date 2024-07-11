package com.example.receipt.recorder.ui

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.load
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.repository.ReceiptRepository
import com.example.receipt.recorder.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReceiptDetailsViewModel(val receipt: Receipt): ViewModel(), KoinComponent {
    private val receiptRepository: ReceiptRepository by inject()

    private val _receiptSaved = MutableStateFlow(false)
    val receiptSaved =_receiptSaved.asStateFlow()


    fun loadImage(view: ImageView) {
        view.load(receipt.uri)
    }

    fun updateReceipt(date: Long, total: Double, currency: String, notes: String) {
        val newReceipt = receipt.copy(
            date = date,
            total = total,
            currency = currency,
            notes = notes
        )
        viewModelScope.launch {

            when (newReceipt.receiptId) {
                0L -> when (receiptRepository.createReceipt(newReceipt)) {
                    is Result.Success -> _receiptSaved.value = true
                    else -> // Bubble error
                        _receiptSaved.value = true
                }
                else -> when (receiptRepository.updateReceipt(newReceipt)) {
                    is Result.Success -> _receiptSaved.value = true
                    else -> // Bubble error
                        _receiptSaved.value = true
                }
            }
        }
    }
}