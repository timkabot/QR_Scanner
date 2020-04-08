package com.app.qrscanner.ui.createQr

import android.os.Bundle
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.data.CodesRepository
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.ui.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_create.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Router

class CreateCodeFragment : BaseFragment() {
    private val codesRepository by inject<CodesRepository>()
    private val router by inject<Router>()
    override val layoutRes = R.layout.fragment_create

    private fun initListeners() {
        createCodeButton.setOnClickListener {
            editText.text.let {
                if (it.isNotEmpty()) {
                    saveCodeInDatabase(Code(data = it.toString(), type = CodeType.CREATED))
                    router.navigateTo(Screens.ShowQRScreen(editText.text.toString()))
                }
            }
        }
    }

    private fun saveCodeInDatabase(code: Code) {
        Thread {
            codesRepository.insertCode(code)
        }.also {
            it.start()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
    }
}