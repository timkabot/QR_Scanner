package com.app.qrscanner.domain.entities

import androidx.room.TypeConverter

enum class CodeType {
    ADDRESSBOOK, //DONE
    EMAIL_ADDRESS, //DONE
    URI, //DONE
    FACEBOOK,
    WHATSAPP,
    YOUTUBE,
    PAYPAL,
    CARD,
    VIBER,
    INSTAGRAM,
    TWITTER,
    SPOTIFY,
    TEXT, //DONE
    VIN, //DONE
    GEO,
    TEL,
    SMS, //DONE
    CALENDAR,
    WIFI;

    class Converter {
        @TypeConverter
        fun toInt(codeType: CodeType): Int = codeType.ordinal

        @TypeConverter
        fun fromInt(int: Int): CodeType = CodeType.values()[int]
    }
}