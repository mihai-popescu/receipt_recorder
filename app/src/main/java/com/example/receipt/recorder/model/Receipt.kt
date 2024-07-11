package com.example.receipt.recorder.model

import java.net.URI
import java.util.Date

data class Receipt (
    val receiptId: Long,
    val uri: URI,
    val date: Long,
    val total: Double,
    val currency: String,
    val notes: String
)