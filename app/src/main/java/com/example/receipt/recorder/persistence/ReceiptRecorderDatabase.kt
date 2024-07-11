package com.example.receipt.recorder.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.receipt.recorder.model.persistence.ReceiptEntity


object ReceiptRecorderDatabaseRoomBuilder {

    fun databaseBuilder(context: Context): RoomDatabase.Builder<ReceiptRecorderDatabaseRoom> =
        Room.databaseBuilder(context, ReceiptRecorderDatabaseRoom::class.java, "receipt_recorder_database")
}

@Database(version = 1, entities = [ReceiptEntity::class])
abstract class ReceiptRecorderDatabaseRoom : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}
