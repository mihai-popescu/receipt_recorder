package com.example.receipt.recorder.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.model.persistence.ReceiptEntity
import com.example.receipt.recorder.persistence.PersistenceNotFound
import com.example.receipt.recorder.persistence.ReceiptRecorderDatabaseRoom
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

interface ReceiptRepository {

    suspend fun getReceipts(): Flow<Result<List<Receipt>>>

    suspend fun createReceipt(receipt: Receipt): Result<Receipt>

    suspend fun updateReceipt(receipt: Receipt): Result<Receipt>
}

class ReceiptRepositoryImpl(
    private val databaseRoom: ReceiptRecorderDatabaseRoom,
    private val mapperFacade: ReceiptMapperFacade
) : ReceiptRepository, KoinComponent {
    private val coroutineContextStorage: CoroutineContext by lazy { IO + Job() }

    private val receiptDao get() = databaseRoom.receiptDao()

    override suspend fun getReceipts(): Flow<Result<List<Receipt>>> {
        return loadReceipts().flowOn(IO).map {
            it?.let { receiptEntities ->
                val receipts = receiptEntities.map { receiptEntity ->
                    mapperFacade.mapReceiptEntityToReceipt(receiptEntity)
                }
                Result.Success(receipts) as Result<List<Receipt>>
            } ?: throw PersistenceNotFound()
        }.catch { exception ->
            println(exception.toString())
            emit(Result.Error(Exception()))
        }
    }

    override suspend fun createReceipt(receipt: Receipt): Result<Receipt> {
        var result: Result<Receipt>?
        withContext(IO) {
            val mappedReceipt = mapperFacade.mapReceiptToReceiptEntity(receipt)
            result = insertReceipt(mappedReceipt)?.let {
                Result.Success(mapperFacade.mapReceiptEntityToReceipt(it))
            }
        }

        return result ?: Result.Error(Exception())
    }

    override suspend fun updateReceipt(receipt: Receipt): Result<Receipt> {
        return withContext(IO) {
            val mappedReceipt = mapperFacade.mapReceiptToReceiptEntity(receipt)
            insertReceipt(mappedReceipt)?.let { Result.Success(receipt) }
                ?: Result.Error(Exception())
        }
    }

    private suspend fun loadReceipts(): Flow<List<ReceiptEntity>?> {
        return withContext(coroutineContextStorage) {
            receiptDao.getReceipts()
        }
    }

    private suspend fun insertReceipt(receipt: ReceiptEntity): ReceiptEntity? {
        return withContext(coroutineContextStorage) {
            databaseRoom.withTransaction {
                try {
                    receiptDao.insertReceipt(receipt).firstOrNull()?.let { receiptId ->
                        receipt.copy(receiptId = receiptId)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message ?: ex.toString())
                    null
                }
            }
        }
    }

    companion object {
        const val TAG = "ReceiptRepository"
    }
}