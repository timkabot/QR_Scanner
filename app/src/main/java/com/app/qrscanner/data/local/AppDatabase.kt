package com.app.qrscanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeType


@Database(entities = [Code::class], version = 2)
@TypeConverters(CodeType.Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun codesDao(): CodesDao

    companion object {
        fun build(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "qr_codes-db.db"
        ).build()
    }

}