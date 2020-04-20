package com.app.qrscanner.presentation.global

import net.glxn.qrgen.core.scheme.Schema

abstract class CreateCodeBaseFragment : BaseFragment() {
    abstract fun createCode(): Pair<String , Schema>
}