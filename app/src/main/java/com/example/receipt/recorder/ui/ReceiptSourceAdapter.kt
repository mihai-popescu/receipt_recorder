package com.example.receipt.recorder.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.receipt.recorder.model.Receipt

fun interface ReceiptSourceAdapterListener {
    fun onClick(receipt: Receipt)
}

class ReceiptSourceAdapter(
    private val listener: ReceiptSourceAdapterListener?
) : ListAdapter<Receipt, ReceiptItemViewHolder>(RECEIPT_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemViewHolder {
        return ReceiptItemViewHolder.create(parent, object : ReceiptSourceAdapterListener {
            override fun onClick(receipt: Receipt) {
                listener?.onClick(receipt)
            }
        })
    }

    override fun onBindViewHolder(holder: ReceiptItemViewHolder, position: Int) {
        getItem(position)?.let { receipt ->
            holder.bind(receipt)
        }
    }

    companion object {
        val RECEIPT_COMPARATOR = object : DiffUtil.ItemCallback<Receipt>() {
            override fun areContentsTheSame(oldItem: Receipt, newItem: Receipt): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Receipt, newItem: Receipt): Boolean =
                oldItem == newItem
        }
    }



    fun updateData(receipts: List<Receipt>, onSubmit: (() -> Unit)? = null) {
        submitList(receipts) {
            onSubmit?.invoke()
        }
    }
}