package com.app.qrscanner.presentation.history.scannedCodesHistory

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.domain.interactors.DatabaseInteractor
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.list.CodesAdapter
import kotlinx.android.synthetic.main.fragment_history_scanned.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Router

class ScannedCodesFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_history_scanned
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codesAdapter: CodesAdapter

    private val databaseInteractor by inject<DatabaseInteractor>()
    private val router by inject<Router>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecycler()
        getData()
    }

    private fun initRecycler() {
        codesAdapter = CodesAdapter(arrayListOf(), object : onCodeClickListener {
            override fun onClick(code: Code) {
                router.navigateTo(Screens.ShowScannedQRScreen(SerializableResult(result = code.result)))
            }
        })
        linearLayoutManager = LinearLayoutManager(context)

        with(recycler) {
            layoutManager = linearLayoutManager
            adapter = codesAdapter
        }
    }

    private fun getData() {
        databaseInteractor.getScannedCodes().observeForever {
            if (it.isEmpty()) {
                emptyImage?.let { emptyImageView ->
                    emptyImageView.visibility = View.VISIBLE
                }
            } else {
                emptyImage?.let { emptyImageView ->
                    emptyImageView.visibility = View.GONE
                }
                updateAdapter(it)
            }
        }
    }

    private fun updateAdapter(newCodes: List<Code>) {
        with(codesAdapter) {
            codes.clear()
            codes.addAll(newCodes.reversed())
            notifyDataSetChanged()
        }
    }
}

interface onCodeClickListener {
    fun onClick(code: Code) {}
}