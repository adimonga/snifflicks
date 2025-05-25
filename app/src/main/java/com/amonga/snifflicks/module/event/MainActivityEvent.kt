package com.amonga.snifflicks.module.event

import com.amonga.snifflicks.core.compose.interfaces.IEvent

sealed interface MainActivityEvent: IEvent {
    data object ShowPasswordDialog : MainActivityEvent
    data object DismissPasswordDialog : MainActivityEvent
    data class OnPasswordVerify(val password: String) : MainActivityEvent
}