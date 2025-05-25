package com.amonga.snifflicks.core.compose.utility.composeutils

import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import com.amonga.snifflicks.R
import com.amonga.snifflicks.core.util.ResourceUtils
import timber.log.Timber

object ColorConversionUtils {

	fun safeParseToComposeColor(colorInHex: String?, @ColorRes default: Int = R.color.black): Color {
		return try {
			Color(android.graphics.Color.parseColor(colorInHex))
		} catch (t: Throwable) {
			Color(ResourceUtils.getColor(default))
		}
	}

	fun getColorFromHex(colorString: String?, defaultColor: Color = Color.Black): Color {
		try {
			if (colorString != null) {
				return Color(android.graphics.Color.parseColor(colorString))
			}
		} catch (t: Throwable) {
			Timber.e(t)
		}
		return defaultColor
	}

	fun getColorFromHexOrNull(colorString: String?, defaultColor: Color?): Color? {
		try {
			if (colorString != null) {
				return Color(android.graphics.Color.parseColor(colorString))
			}
		} catch (t: Throwable) {
			Timber.e(t)
		}
		return defaultColor
	}
}
