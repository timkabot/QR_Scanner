package com.app.qrscanner.domain.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.Result

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
        fun fromInt(int: Int): CodeType = values()[int]

        @TypeConverter
        fun fromResult(value: Result?): String {
            val gson = Gson()
            return gson.toJson(value)

        }

        @TypeConverter
        fun toResult(str: String): Result? {
            val listType = object : TypeToken<Result>() {}.type
            return Gson().fromJson(str, listType)
        }
    }
}