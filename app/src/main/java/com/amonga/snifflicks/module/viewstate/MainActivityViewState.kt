package com.amonga.snifflicks.module.viewstate

import com.amonga.snifflicks.core.compose.interfaces.IViewState

data class MainActivityViewState(
    val isPasswordDialogVisible: Boolean = false
) : IViewState {


    companion object {
        val INITIAL = MainActivityViewState()
    }
}