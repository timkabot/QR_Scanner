package com.app.qrscanner.domain.interactors

import com.app.qrscanner.utils.getValue
import com.app.qrscanner.utils.isNotNullOrEmpty
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.GeoParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.TelParsedResult
import it.auron.library.vcard.VCard
import it.auron.library.vcard.VCardParser
import it.auron.library.vevent.VEvent
import it.auron.library.vevent.VEventParser
import it.auron.library.wifi.WifiCard
import it.auron.library.wifi.WifiCardParser
import net.glxn.qrgen.core.scheme.EMail
import net.glxn.qrgen.core.scheme.GeoInfo
import net.glxn.qrgen.core.scheme.SMS
import net.glxn.qrgen.core.scheme.Telephone
import java.text.SimpleDateFormat
import java.util.*

class MyResultParser(val resultInteractor: ParsedResultInteractor) {
    val res = StringBuilder()
    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    lateinit var result: ParsedResult
    private fun parseVCard(value: VCard): String {
        res.append("Тип: контакт \n")
        if (value.name.isNotNullOrEmpty()) {
            res.append("Имя: ${value.name}\n")
        }
        if (value.address.isNotNullOrEmpty()) {
            res.append("Адрес: ${value.name}\n")
        }
        if (value.company.isNotNullOrEmpty()) {
            res.append("Компания: ${value.name}\n")
        }

        if (value.emails.isNotNullOrEmpty()) {
            res.append("Компания: ${value.emails.getValue()}\n")
        }
        if (value.telephones.isNotNullOrEmpty()) {
            res.append("Телефон: ${value.telephones.getValue()}\n")
        }
        if (value.title.isNotNullOrEmpty()) {
            res.append("Заголовок: ${value.title}")
        }
        return res.toString()
    }

    private fun parseWifi(wifi: WifiCard): String {
        res.append("Тип: WIFI \n")
        if (wifi.sid.isNotNullOrEmpty()) {
            res.append("Имя сети: ${wifi.sid}\n")
        }
        if (wifi.password.isNotNullOrEmpty()) {
            res.append("Пароль: ${wifi.password}\n")
        }
        if (wifi.type.isNotNullOrEmpty()) {
            res.append("Тип защиты: ${wifi.type}")
        }
        return res.toString()
    }

    private fun parseVEvent(vEvent: VEvent): String {
        res.append("Тип: календарь \n")
        if (vEvent.summary.isNotNullOrEmpty()) {
            res.append("Название: ${vEvent.summary}\n")
        }
        if (vEvent.location.isNotNullOrEmpty()) {
            res.append("Локация: ${vEvent.location}\n")
        }


        if (vEvent.dtStart.isNotNullOrEmpty()) {
            println("date start found")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = vEvent.dtStart.toLong()
            res.append("Дата начала: ${simpleDateFormat.format(calendar.time)}\n")
        }
        if (vEvent.dtEnd.isNotNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = vEvent.dtEnd.toLong()
            res.append("Дата окончания: ${simpleDateFormat.format(calendar.time)}")
        }
        return res.toString()
    }

    private fun parseSms(sms: SMS): String {
        res.append("Тип: SMS \n")
        if (sms.number.isNotNullOrEmpty()) {
            res.append("Номер: ${sms.number}\n")
        }

        if (sms.subject.isNotNullOrEmpty()) {
            res.append("Сообщение: ${sms.subject}\n")
        }
        return res.toString()
    }

    fun appropriateTextFromQrValue(qr: String): String {

        if (VCardParser.isVCard(qr)) {
            return parseVCard(VCardParser.parse(qr))
        }

        if (WifiCardParser.isWifi(qr)) {
            return parseWifi(WifiCardParser.parse(qr))
        }

        if (VEventParser.isVEvent(qr)) {
            println("Trying to parse ${qr}")
            return parseVEvent(VEventParser.parse(qr))
        }

        try {
            val sms = SMS.parse(qr)
            return parseSms(sms)
        } catch (e: Exception) {
        }

        try {
            EMail.parse(qr)
            return resultInteractor.getInfoForEmail(result as EmailAddressParsedResult)
        } catch (e: Exception) {
        }

        try {
            GeoInfo.parse(qr)
            return resultInteractor.getInfoForGeo(result as GeoParsedResult)
        } catch (e: Exception) {
        }

        try {
            Telephone.parse(qr)
            return resultInteractor.getInfoForTel(result as TelParsedResult)
        } catch (e: Exception) {
        }

        return qr
    }
}