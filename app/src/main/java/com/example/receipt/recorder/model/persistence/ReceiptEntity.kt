package com.example.receipt.recorder.model.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val receiptId: Long,
    val uri: String,
    val date: Long,
    val total: Double,
    val currency: String,
    val notes: String
)