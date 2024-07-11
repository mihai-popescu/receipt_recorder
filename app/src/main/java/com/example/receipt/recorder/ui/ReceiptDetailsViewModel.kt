package com.example.receipt.recorder.ui

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import coil.load
import com.example.receipt.recorder.model.Receipt

class ReceiptDetailsViewModel(private val receipt: Receipt): ViewModel() {

    fun loadImage(view: ImageView) {
        view.load(receipt.uri)
    }
}