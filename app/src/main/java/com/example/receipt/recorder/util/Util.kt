package com.example.receipt.recorder.util


import android.content.Context
import android.content.Intent
import android.net.Uri

inline fun <A, B, R> ifNotNull(a: A?, b: B?, code: (A, B) -> R): R? =
    if (a != null && b != null) {
        code(a, b)
    } else null

fun addMediaToGallery(mediaUri: Uri, context: Context?) {
    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
        mediaScanIntent.data = mediaUri
        context?.sendBroadcast(mediaScanIntent)
    }
}