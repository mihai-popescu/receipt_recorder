package com.example.receipt.recorder

import android.net.Uri
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.repository.ReceiptRepository
import com.example.receipt.recorder.repository.Result
import com.example.receipt.recorder.ui.ReceiptDetailsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class ReceiptDetailsViewModelTest: KoinTest {
    private val newReceipt = Receipt(uri = Uri.parse("anystring"))
    private val viewModel = ReceiptDetailsViewModel(newReceipt)


    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setUp() {
        stopKoin()
        startKoin { modules(
            module {
            }
        )}
    }

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

    @Test
    fun receiptDetailsViewModelTest_updateReceiptCreate() {
        val  mock = declareMock<ReceiptRepository>()
        mock.stub {
            onBlocking { createReceipt(newReceipt) }.doReturn(Result.Success(newReceipt.copy(receiptId = 1L)))
        }
        val viewModel = ReceiptDetailsViewModel(newReceipt)
        assertFalse(viewModel.receiptSaved.value)
        viewModel.updateReceipt()
        assertTrue(viewModel.receiptSaved.value)
    }

    @Test
    fun receiptDetailsViewModelTest_updateReceiptFailed() {
        val  mock = declareMock<ReceiptRepository>()
        mock.stub {
            onBlocking { createReceipt(newReceipt) }.doReturn(Result.Error(Exception()))
        }
        val viewModel = ReceiptDetailsViewModel(newReceipt)
        assertFalse(viewModel.receiptSaved.value)
        viewModel.updateReceipt()
        assertTrue(viewModel.receiptSaved.value)
    }

    @Test
    fun receiptDetailsViewModelTest_updateReceiptUpdate() {
        val updateReceipt = Receipt(1, Uri.parse("sometext"))
        val  mock = declareMock<ReceiptRepository>()
        mock.stub {
            onBlocking { updateReceipt(updateReceipt) }.doReturn(Result.Success(updateReceipt))
        }
        val viewModel = ReceiptDetailsViewModel(updateReceipt)
        assertFalse(viewModel.receiptSaved.value)
        viewModel.updateReceipt()
        assertTrue(viewModel.receiptSaved.value)
    }

    @Test
    fun receiptDetailsViewModelTest_updateReceiptUpdateFailed() {
        val updateReceipt = Receipt(1, Uri.parse("sometext"))
        val  mock = declareMock<ReceiptRepository>()
        mock.stub {
            onBlocking { createReceipt(updateReceipt) }.doReturn(Result.Error(Exception()))
        }
        val viewModel = ReceiptDetailsViewModel(updateReceipt)
        assertFalse(viewModel.receiptSaved.value)
        viewModel.updateReceipt()
        assertTrue(viewModel.receiptSaved.value)
    }

    @After
    fun after() {
        stopKoin()
    }
}