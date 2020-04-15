package com.app.qrscanner.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "codes")
data class Code(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,
    val data: String,
    @TypeConverters(CodeStatus.Converter::class)
    val status: CodeStatus,
    val shortDescription: String = "",
    val qrCodePath: String = "",
    @TypeConverters(CodeType.Converter::class)
    val type: CodeType = CodeType.TEXT
)