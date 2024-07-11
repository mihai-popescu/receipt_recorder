package com.example.receipt.recorder.model

import android.net.Uri

data class Receipt (
    val receiptId: Long,
    val uri: Uri,
    val date: Long,
    val total: Double,
    val currency: String,
    val notes: String
)