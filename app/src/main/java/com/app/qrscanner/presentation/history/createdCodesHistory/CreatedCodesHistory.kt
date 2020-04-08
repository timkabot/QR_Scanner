package com.app.qrscanner.presentation.history.createdCodesHistory

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.list.CodesAdapter
import kotlinx.android.synthetic.main.fragment_history_created.*
import org.koin.android.ext.android.inject

class CreatedCodesHistory :BaseFragment(){
    override val layoutRes = R.layout.fragment_history_created
    private val codesRepository by inject<CodesRepository>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codesAdapter: CodesAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecycler()
        getData()
    }

    private fun initRecycler() {
        codesAdapter = CodesAdapter(arrayListOf())
        linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
        recycler.adapter = codesAdapter
    }

    private fun getData() {
        codesRepository.getCreatedCodes().observeForever {
            if(it.isEmpty()){
                emptyImage?.let { emptyImageView ->
                    emptyImageView.visibility = View.VISIBLE
                }
            }
            else {
                emptyImage?.let { emptyImageView ->
                    emptyImageView.visibility = View.GONE
                    updateAdapter(it)
                }
            }
        }
    }


    private fun updateAdapter(codes : List<Code>){
        codesAdapter.codes.clear()
        codesAdapter.codes.addAll(codes.reversed())
        codesAdapter.notifyDataSetChanged()
    }
}