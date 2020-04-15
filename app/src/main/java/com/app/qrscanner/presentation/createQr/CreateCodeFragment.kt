package com.app.qrscanner.presentation.createQr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.SerializableResult
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.list.CodeTypesAdapter
import kotlinx.android.synthetic.main.fragment_create.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Router

class CreateCodeFragment : BaseFragment() {
    private val codesRepository by inject<CodesRepository>()
    private val router by inject<Router>()
    override val layoutRes = R.layout.fragment_create
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var codesTypesAdapter: CodeTypesAdapter
    private val activity
        get() = getActivity() as ContainerActivity

    private fun initListeners() {
        createCodeButton.setOnClickListener {
            editText.text.let {
                if (it.isNotEmpty()) {
                    saveCodeInDatabase(Code(data = it.toString(), status = CodeStatus.CREATED))
                    router.navigateTo(Screens.ShowCreatedQRScreen(editText.text.toString(), SerializableResult()))
                }
            }
        }

        pasteButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData? = clipboard.primaryClip
            clipData?.apply {
                val textToPaste:String = this.getItemAt(0).text.toString().trim()
                editText.setText(textToPaste)
            }
        }
    }

    private fun initRecycler() {
        codesTypesAdapter = CodeTypesAdapter(CodeType.values().toList().filter { it != CodeType.URI })
        gridLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = codesTypesAdapter
    }
    private fun saveCodeInDatabase(code: Code) {
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    override fun onResume() {
        changeToolbar(true)
        super.onResume()
    }
    private fun changeToolbar(started: Boolean) {
        if (started) {

            ContainerActivity.setAppBatTitle("", activity)
            ContainerActivity.changeSettingButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeAdsButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeNoAdsButtonVisibility(activity, View.GONE)
            ContainerActivity.changeBackButtonVisibility(activity, View.VISIBLE)

        } else {
            ContainerActivity.setAppBatTitle("", activity)
            ContainerActivity.changeBackButtonVisibility(activity, View.VISIBLE)

        }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
        initRecycler()
    }
}