package com.app.qrscanner.ui.history.scannedCodesHistory

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.ui.global.BaseFragment
import com.app.qrscanner.ui.global.list.CodesAdapter
import kotlinx.android.synthetic.main.fragment_history_scanned.*
import org.koin.android.ext.android.inject

class ScannedCodesFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_history_scanned
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codesAdapter: CodesAdapter

    val codesRepository by inject<CodesRepository>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        codesAdapter = CodesAdapter(arrayListOf())
        linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
        recycler.adapter = codesAdapter
        getData()
    }

    private fun getData() {
        codesRepository.getScannedCodes().observeForever {
            if(it.isEmpty()){
                emptyImage.visibility = View.VISIBLE
            }
            else {
                emptyImage.visibility = View.GONE

                codesAdapter.codes.clear()
                it.reversed().forEach{ code ->
                    codesAdapter.codes.add(code)
                }
                codesAdapter.notifyDataSetChanged()
            }
        }
    }

}