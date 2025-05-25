package com.amonga.snifflicks.core.compose.utility.extensions

import android.app.Activity
import android.view.Window
import androidx.annotation.ColorRes
import androidx.core.view.WindowInsetsControllerCompat
import com.amonga.snifflicks.core.util.ResourceUtils

fun Activity.setStatusBarColorData(@ColorRes color: Int, isLightTheme: Boolean = true) {
    val window: Window = window

    val wic = WindowInsetsControllerCompat(window, window.decorView)
    wic.isAppearanceLightStatusBars = isLightTheme

    window.statusBarColor = ResourceUtils.getColor(color)

}
