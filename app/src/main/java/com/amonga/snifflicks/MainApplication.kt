package com.amonga.snifflicks

import com.amonga.snifflicks.core.compose.application.BaseComposeApplication
import com.amonga.snifflicks.core.util.ResourceUtils
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Aditya on 21/05/25.
 */

@HiltAndroidApp
class MainApplication : BaseComposeApplication() {

    override fun onCreate() {
        super.onCreate()
        ResourceUtils.init(this)
        app = this
    }

    companion object {
        private lateinit var app: MainApplication

        fun getApplication(): MainApplication {
            return app
        }
    }
}