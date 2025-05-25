package com.amonga.snifflicks.module.viewresult

import com.amonga.snifflicks.core.compose.interfaces.IViewResult

sealed interface MainActivityViewResult : IViewResult {
    data object ShowPasswordDialog : MainActivityViewResult
    data object HidePasswordDialog : MainActivityViewResult
}