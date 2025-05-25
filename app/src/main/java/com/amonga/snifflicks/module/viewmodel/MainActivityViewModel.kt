package com.amonga.snifflicks.module.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import com.amonga.snifflicks.R
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.core.util.AESUtil
import com.amonga.snifflicks.di.modules.NetworkModule.Companion.EncryptedApiKey
import com.amonga.snifflicks.di.modules.NetworkModule.Companion.getOrSetApiKey
import com.amonga.snifflicks.module.event.MainActivityEvent
import com.amonga.snifflicks.module.sideeffect.MainActivitySideEffect.OpenLanguageSelectionFragment
import com.amonga.snifflicks.module.viewresult.MainActivityViewResult
import com.amonga.snifflicks.module.viewstate.MainActivityViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: EncryptedSharedPreferences
) : BaseViewModel<MainActivityEvent, MainActivityViewResult, MainActivityViewState>(
    MainActivityViewState.INITIAL
) {

    init {
        processEvent(MainActivityEvent.ShowPasswordDialog)
    }

    override suspend fun HandleEventScope.handleEvent(
        event: MainActivityEvent
    ) {
        when (event) {
            MainActivityEvent.ShowPasswordDialog -> {
                encryptedSharedPreferences.getString("password", null)?.let {
                    try {
                        val result = AESUtil.decrypt(EncryptedApiKey, it)
                        getOrSetApiKey("Bearer $result")
                        OpenLanguageSelectionFragment.emit()
                    } catch (e: Exception) {
                        null
                    }
                } ?: MainActivityViewResult.ShowPasswordDialog.reduceToState()

            }

            MainActivityEvent.DismissPasswordDialog -> {
                MainActivityViewResult.HidePasswordDialog.reduceToState()
            }

            is MainActivityEvent.OnPasswordVerify -> {
                try {
                    val result = AESUtil.decrypt(EncryptedApiKey, event.password)
                    encryptedSharedPreferences.edit().putString("password", event.password).apply()
                    getOrSetApiKey("Bearer $result")
                    MainActivityViewResult.HidePasswordDialog.reduceToState()
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_verified),
                        Toast.LENGTH_SHORT
                    ).show()

                    OpenLanguageSelectionFragment.emit()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.invalid_password),
                        Toast.LENGTH_SHORT
                    ).show()
                    null
                }
            }
        }
    }

    override fun MainActivityViewResult.reduce(oldState: MainActivityViewState): MainActivityViewState {
        return when (this) {
            MainActivityViewResult.ShowPasswordDialog -> {
                oldState.copy(isPasswordDialogVisible = true)
            }

            MainActivityViewResult.HidePasswordDialog -> {
                oldState.copy(isPasswordDialogVisible = false)
            }
        }
    }
}