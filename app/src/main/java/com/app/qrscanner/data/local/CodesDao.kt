package com.app.qrscanner.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeType

@Dao
interface CodesDao {

    @Query("SELECT * FROM codes WHERE type = :type")
    fun getCodes(type: Int) : LiveData<List<Code>>

    @Insert
    fun insertCode(vararg codes: Code)

    @Update
    fun updateAll(vararg codes: Code)

}