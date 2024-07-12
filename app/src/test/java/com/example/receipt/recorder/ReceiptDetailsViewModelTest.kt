package com.example.receipt.recorder

import android.net.Uri
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.ui.ReceiptDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class ReceiptDetailsViewModelTest: KoinTest {
    private val newReceipt = Receipt(uri = Uri.parse("anystring"))
    private val viewModel = ReceiptDetailsViewModel(newReceipt)

    @Test
    fun receiptDetailsViewModelTest_initialization() {
        val receipt = viewModel.receipt
        assertEquals(newReceipt, receipt)
        assertEquals(newReceipt.date, viewModel.updateDate.value)
        assertFalse(viewModel.receiptSaved.value)
    }

    @Test
    fun receiptDetailsViewModelTest_setNewTotal() {
        val newTotal = 10.0
        viewModel.setNewTotal(newTotal)
        assertEquals(newTotal, viewModel.receipt.total, .0)
    }

    @Test
    fun receiptDetailsViewModelTest_setNewCurrency() {
        val newCurrency = "RON"
        viewModel.setNewCurrency(newCurrency)
        assertEquals(newCurrency, viewModel.receipt.currency)
    }

    @Test
    fun receiptDetailsViewModelTest_setNewNotes() {
        val newNotes = "Test notes"
        viewModel.setNewNotes(newNotes)
        assertEquals(newNotes, viewModel.receipt.notes)
    }

    @Test
    fun receiptDetailsViewModelTest_setNewDate() = runTest {
        val newDate = Calendar.getInstance().timeInMillis
        viewModel.setNewDate(newDate)
        assertEquals(newDate, viewModel.receipt.date)
        assertEquals(newDate, viewModel.updateDate.value)
    }

    @After
    fun after() {
        stopKoin()
    }
}