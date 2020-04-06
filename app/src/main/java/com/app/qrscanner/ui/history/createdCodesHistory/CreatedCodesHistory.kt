package com.app.qrscanner.ui.history.createdCodesHistory

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompatSideChannelService
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.ui.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_history_created.*
import org.koin.android.ext.android.inject

class CreatedCodesHistory :BaseFragment(){
    override val layoutRes = R.layout.fragment_history_created
    private lateinit var linearLayoutManager: LinearLayoutManager
    val codesRepository by inject<CodesRepository>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getData()
    }

    private fun getData() {
        codesRepository.getCreatedCodes().observeForever {
            if(it.isEmpty()){
                emptyImage.visibility = View.VISIBLE
            }
            else {
                emptyImage.visibility = View.GONE
            }
        }
    }
}