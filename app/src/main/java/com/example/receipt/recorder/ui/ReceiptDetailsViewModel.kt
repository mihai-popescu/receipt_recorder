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

class ReceiptDetailsViewModel(receipt: Receipt): ViewModel(), KoinComponent {
    private val _updateDate = MutableStateFlow(receipt.date)
    val updateDate = _updateDate.asStateFlow()

    var receipt = receipt
        private set
    private val receiptRepository: ReceiptRepository by inject()

    private val _receiptSaved = MutableStateFlow(false)
    val receiptSaved =_receiptSaved.asStateFlow()


    fun loadImage(view: ImageView) {
        view.load(receipt.uri)
    }

    fun updateReceipt() {
        viewModelScope.launch {
            when (receipt.receiptId) {
                0L -> when (receiptRepository.createReceipt(receipt)) {
                    is Result.Success -> _receiptSaved.value = true
                    else -> // Bubble error
                        _receiptSaved.value = true
                }
                else -> when (receiptRepository.updateReceipt(receipt)) {
                    is Result.Success -> _receiptSaved.value = true
                    else -> // Bubble error
                        _receiptSaved.value = true
                }
            }
        }
    }

    fun setNewDate(value: Long) {
        receipt = receipt.copy(date = value)
        _updateDate.value =value
    }

    fun setNewCurrency(value: String) {
        receipt = receipt.copy(currency = value)
    }

    fun setNewTotal(value: Double) {
        receipt = receipt.copy(total = value)
    }

    fun setNewNotes(value: String) {
        receipt = receipt.copy(notes = value)
    }
}