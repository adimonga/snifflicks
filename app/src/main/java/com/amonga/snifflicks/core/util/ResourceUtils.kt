package com.amonga.snifflicks.core.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext

object ResourceUtils {

    @ApplicationContext
    private lateinit var context: Context

    fun init(@ApplicationContext context: Context) {
        this.context = context
    }

    fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    fun getString(@StringRes stringRes: Int, vararg args: Any?): String {
        return ContextCompat.getString(context, stringRes).format(args)
    }
}