package com.app.qrscanner.domain.interactors

import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code

class DatabaseInteractor(private val codesRepository: CodesRepository) {

    fun saveCodeInDatabase(code: Code) {
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    fun getScannedCodes() = codesRepository.getScannedCodes()

}