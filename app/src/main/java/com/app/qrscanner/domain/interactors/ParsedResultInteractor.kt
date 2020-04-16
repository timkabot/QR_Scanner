package com.app.qrscanner.domain.interactors

import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.utils.whenNotNull
import com.google.zxing.client.result.*
import com.google.zxing.client.result.ParsedResultType.*

class ParsedResultInteractor(private val codeTypeInteractor: CodeTypeInteractor) {

    fun getInfoForParsedResult(r: ParsedResult) : String{
       var res = ""
       if(r is EmailAddressParsedResult) res = getInfoForEmail(r)
        if(r is AddressBookParsedResult) res = getInfoForAddressBook(r)
        if(r is WifiParsedResult) res = getInfoForWifi(r)
        if(r is URIParsedResult) res = getInfoForURI(r)
        if(r is SMSParsedResult) res = getInfoForSms(r)
        if(r is TelParsedResult) res = getInfoForTel(r)
        if(r is CalendarParsedResult) res = getInfoForCalendar(r)
        if(r is GeoParsedResult) res = getInfoForGeo(r)
        if(r is TextParsedResult) res = getInfoForText(r)
        return res
    }

    fun getInfoForEmail(email: EmailAddressParsedResult): String {
        var info = ""
        with(email) {
            whenNotNull(tos) { if (tos.isNotEmpty()) info += "Получатель: ${tos.contentToString()}\n" }
            whenNotNull(subject) { if (subject.isNotEmpty()) info += "Тема: ${subject}\n" }
            whenNotNull(body) { if (body.isNotEmpty()) info += "Контент: ${body}\n" }
        }
        return info
    }

    fun getInfoForAddressBook(addressBook: AddressBookParsedResult): String {
        var info = ""
        with(addressBook) {
            whenNotNull(names) { if (names.isNotEmpty()) info += "Имя: ${names.contentToString()}\n" }
            whenNotNull(nicknames) { if (nicknames.isNotEmpty()) info += "Прозвище: ${nicknames.contentToString()}\n" }
            whenNotNull(pronunciation) { if (pronunciation.isNotEmpty()) info += "Произношение: ${pronunciation}\n" }
            whenNotNull(phoneNumbers) { if (phoneNumbers.isNotEmpty()) info += "Номер: ${phoneNumbers.contentToString()}\n" }
            whenNotNull(phoneTypes) { if (phoneTypes.isNotEmpty()) info += "Тип номера: ${phoneTypes.contentToString()}\n" }
            whenNotNull(emails) { if (emails.isNotEmpty()) info += "Email: ${emails.contentToString()}\n" }
            whenNotNull(emailTypes) { if (emailTypes.isNotEmpty()) info += "Тип email: ${emailTypes.contentToString()}\n" }
            whenNotNull(geo) { if (geo.isNotEmpty()) info += "Геопозиция : ${geo.contentToString()}\n" }

            whenNotNull(instantMessenger) { if (instantMessenger.isNotEmpty()) info += "Связь: ${instantMessenger}\n" }
            whenNotNull(note) { if (note.isNotEmpty()) info += "Заметки: ${note}\n" }
            whenNotNull(birthday) { if (birthday.isNotEmpty()) info += "День рождения: ${birthday}\n" }
            whenNotNull(title) { if (title.isNotEmpty()) info += "Заголовок: ${title}\n" }

            whenNotNull(addresses) { if (addresses.isNotEmpty()) info += "Адреса: ${addresses.contentToString()}\n" }

        }
        return info
    }

    fun getInfoForWifi(wifi: WifiParsedResult): String {
        var info = ""
        whenNotNull(wifi.ssid) { info += "Имя сети: ${wifi.ssid} \n" }
        whenNotNull(wifi.password) { info += "Пароль: ${wifi.password} \n" }
        whenNotNull(wifi.networkEncryption) { info += "Тип шифрования: ${wifi.networkEncryption}" }
        return info
    }

    fun getInfoForURI(uri: URIParsedResult): String {
        var info = ""
        whenNotNull(uri.uri) { info += "${uri.uri}\n" }
        whenNotNull(uri.title) { info += "Заголовок: ${uri.title}" }
        return info
    }

    fun getInfoForSms(sms: SMSParsedResult): String {
        var info = ""
        whenNotNull(sms.numbers) { info += "Номер: ${sms.numbers?.contentToString()}\n" }
        whenNotNull(sms.subject) { info += "Тема сообщения: ${sms.subject}\n" }
        whenNotNull(sms.body) { info += "Текст сообщения: ${sms.body} " }
        return info
    }

    fun getInfoForTel(tel: TelParsedResult): String {
        var info = ""
        whenNotNull(tel.title) { info += "Заголовок ${tel.title}" }
        whenNotNull(tel.number) { info += "Номер ${tel.number}" }
        return info
    }

    fun getInfoForCalendar(calendar: CalendarParsedResult): String {
        var info = ""
        whenNotNull(calendar.attendees) { info += "Участники: ${calendar.attendees.contentToString()}\n" }
        whenNotNull(calendar.description) { info += "Описание: ${calendar.description}\n" }
        whenNotNull(calendar.organizer) { info += "Организатор ${calendar.organizer}\n" }
        whenNotNull(calendar.start) {
            info += "Начало: ${calendar.start}\n"
        }
        whenNotNull(calendar.end) { info += "Конец: ${calendar.end}\n" }
        whenNotNull(calendar.location) { info += "Местоположение: ${calendar.location}" }
        return info
    }

    fun getInfoForGeo(geo: GeoParsedResult): String {
        var info = ""
        whenNotNull(geo.altitude) { info += "Высота над уровнем моря: ${geo.altitude}\n" }
        whenNotNull(geo.longitude) { info += "Долгота: ${geo.longitude}\n" }
        whenNotNull(geo.latitude) { info += "Широта: ${geo.latitude}\n" }
        return info
    }

    fun getInfoForText(text: TextParsedResult): String {
        var info = ""
        whenNotNull(text.language) { info += "Язык: ${text.language}\n" }
        whenNotNull(text.text) { info += text.text }
        return info
    }

    fun getCodeTypeForParsedResult(parsedResult: ParsedResult): CodeType {
        return when (parsedResult.type) {
            ADDRESSBOOK -> CodeType.ADDRESSBOOK
            EMAIL_ADDRESS -> CodeType.EMAIL_ADDRESS
            PRODUCT -> CodeType.VIN
            GEO -> CodeType.GEO
            URI -> codeTypeInteractor.getSiteType(parsedResult.displayResult)
            TEXT -> CodeType.TEXT
            TEL -> CodeType.TEL
            SMS -> CodeType.SMS
            CALENDAR -> CodeType.CALENDAR
            WIFI -> CodeType.WIFI
            ISBN -> CodeType.TEXT
            VIN -> CodeType.VIN
            else -> CodeType.VIN
        }
    }

}