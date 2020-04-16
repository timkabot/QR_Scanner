package com.app.qrscanner.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.zxing.Result

@Entity(tableName = "codes")
data class Code(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,
    val data: String,
    @TypeConverters(CodeStatus.Converter::class)
    val status: CodeStatus,
    var shortDescription: String = "",
    val qrCodePath: String = "",
    @TypeConverters(CodeStatus.Converter::class)
    var result: Result? = null,
    @TypeConverters(CodeType.Converter::class)
    var type: CodeType = CodeType.TEXT
)