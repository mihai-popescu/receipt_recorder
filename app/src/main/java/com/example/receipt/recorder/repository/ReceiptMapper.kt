package com.example.receipt.recorder.repository

import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.model.persistence.ReceiptEntity
import java.net.URI

class ReceiptMapperFacade(
    val mapReceiptEntityToReceipt: (ReceiptEntity) -> Receipt,
    val mapReceiptToReceiptEntity: (Receipt) -> ReceiptEntity
)

object ReceiptMapperFacadeFactory {
    fun createReceiptMapperFacade(): ReceiptMapperFacade =
        ReceiptMapperFacade(makeReceiptDataMapper(), makeReceiptEntityDataMapper())

    private fun makeReceiptDataMapper(): (ReceiptEntity) -> Receipt = { receiptEntity ->
        makeReceipt(receiptEntity)
    }

    private fun makeReceiptEntityDataMapper(): (Receipt) -> ReceiptEntity = { receipt ->
        makeReceiptEntity(receipt)
    }

    private fun makeReceipt(input: ReceiptEntity): Receipt = Receipt(
        input.receiptId,
        URI.create(input.uri),
        input.date,
        input.total,
        input.currency,
        input.notes
    )

    private fun makeReceiptEntity(input: Receipt) = ReceiptEntity(
        input.receiptId,
        input.uri.path,
        input.date,
        input.total,
        input.currency,
        input.notes
    )
}

