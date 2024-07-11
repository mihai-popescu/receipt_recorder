package com.example.receipt.recorder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.receipt.recorder.databinding.ReceiptItemBinding
import com.example.receipt.recorder.model.Receipt

class ReceiptItemViewHolder(binding: ReceiptItemBinding, private val listener: ReceiptSourceAdapterListener?) :
    RecyclerView.ViewHolder(binding.root) {
    private val thumbnail: ImageView = binding.receiptItemThumbnail
    private var receipt: Receipt? = null

    init {
        binding.root.setOnClickListener {
            receipt?.let { listener?.onClick(it) }
        }
    }

    fun bind(receipt: Receipt) {
        this.receipt = receipt
        this.thumbnail.load(receipt.uri)
    }

    companion object {
        fun create(parent: ViewGroup, listener: ReceiptSourceAdapterListener?): ReceiptItemViewHolder {
            val binding = ReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiptItemViewHolder(binding, listener)
        }
    }
}

