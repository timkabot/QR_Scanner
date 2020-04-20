package com.app.qrscanner.domain.entities

import net.glxn.qrgen.core.scheme.Schema

class MailSchema : Schema(){
    var receiver = ""
    var subject = ""
    var body = ""
    override fun generateString(): String {
        return "mailto:$receiver?subject=${subject}&body=${body}"
    }

    override fun parseSchema(code: String?): Schema {
        return MailSchema()
    }

}