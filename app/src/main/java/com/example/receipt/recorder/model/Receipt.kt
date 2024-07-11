package com.example.receipt.recorder.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class Receipt (
    val receiptId: Long = 0,
    val uri: Uri,
    val date: Long = Calendar.getInstance().timeInMillis,
    val total: Double = .0,
    val currency: String = "",
    val notes: String = ""
): Parcelable