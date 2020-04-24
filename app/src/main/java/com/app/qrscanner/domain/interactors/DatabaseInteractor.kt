package com.app.qrscanner.domain.interactors

import android.content.Context
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.utils.showToast

class DatabaseInteractor(private val codesRepository: CodesRepository, val context: Context) {

    fun saveCodeInDatabase(code: Code) {
        try{
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }}
        catch (e: Exception){
            context.getString(R.string.could_not_save_qr_to_database).showToast(context)
        }
    }

    fun getScannedCodes() = codesRepository.getScannedCodes()

}