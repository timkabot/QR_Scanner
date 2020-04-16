package com.app.qrscanner.domain.entities

data class Contact(
    val number: String? = "",
    val email: String? = "",
    val name: String? = "",
    val notes: String? = "",
    val postal: String? = "",
    val company: String? = ""
)