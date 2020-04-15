package com.app.qrscanner.domain.entities

import androidx.room.TypeConverter

 enum class CodeStatus {
    SCANNED, CREATED;
    class Converter {
        @TypeConverter
        fun toInt(userStatus: CodeStatus): Int = userStatus.ordinal

        @TypeConverter
        fun fromInt(int: Int): CodeStatus = values()[int]
    }
}