package com.app.qrscanner.domain.entities

import androidx.room.TypeConverter

 enum class CodeType {
    SCANNED, CREATED;
    class Converter {
        @TypeConverter
        fun toInt(userType: CodeType): Int = userType.ordinal

        @TypeConverter
        fun fromInt(int: Int): CodeType = values()[int]
    }
}