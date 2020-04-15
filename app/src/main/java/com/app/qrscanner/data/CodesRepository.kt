package com.app.qrscanner.data

import com.app.qrscanner.data.local.AppDatabase
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus

class CodesRepository(db: AppDatabase) {
    private val dao = db.codesDao()

    fun insertCode(code: Code) = dao.insertCode(code)

    fun getScannedCodes() = dao.getCodes(CodeStatus.SCANNED.ordinal)

    fun getCreatedCodes() = dao.getCodes(CodeStatus.CREATED.ordinal)

}