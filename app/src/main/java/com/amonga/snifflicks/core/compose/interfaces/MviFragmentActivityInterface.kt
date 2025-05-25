package com.amonga.snifflicks.core.compose.interfaces

import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel

interface MviFragmentActivityInterface {

	fun onSideEffectReceived(sideEffect: ISideEffect)

	fun getVMInstance(): BaseViewModel<*, *, *>
}
