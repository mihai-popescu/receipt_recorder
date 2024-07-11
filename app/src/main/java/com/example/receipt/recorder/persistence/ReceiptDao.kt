package com.example.receipt.recorder.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.receipt.recorder.model.persistence.ReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(vararg receipt: ReceiptEntity): List<Long>

    @Update
    suspend fun updateReceipt(vararg receipt: ReceiptEntity)

    @Query("SELECT * FROM ReceiptEntity")
    fun getReceipts(): Flow<List<ReceiptEntity>?>
}