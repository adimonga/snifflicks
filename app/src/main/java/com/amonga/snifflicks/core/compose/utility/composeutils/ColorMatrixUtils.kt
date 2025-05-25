package com.amonga.snifflicks.core.compose.utility.composeutils

import androidx.compose.ui.graphics.ColorMatrix

object ColorMatrixUtils {

	/**
	 * Represents a single row in a `ColorMatrix`, used to define how an output channel is calculated.
	 *
	 * Example: `0.33f, 0.33f, 0.33f, 0f, 0f`
	 *
	 * Breakdown of each value:
	 * - **0.33f (Red):** 33% of the input Red channel contributes to the output channel.
	 * - **0.33f (Green):** 33% of the input Green channel contributes to the output channel.
	 * - **0.33f (Blue):** 33% of the input Blue channel contributes to the output channel.
	 * - **0f (Alpha):** The input Alpha channel does not affect this channel (0% contribution).
	 * - **0f (Offset):** No constant value is added to the final output.
	 *
	 * This row effectively averages the input Red, Green, and Blue channels for grayscale conversion
	 * while ignoring the Alpha channel.
	 */

	fun getGreyscaleColorMatrix(): ColorMatrix {
		val redContribution = 0.33f
		val greenContribution = 0.33f
		val blueContribution = 0.33f
		val alphaUnchanged = 1f

		return ColorMatrix(
			floatArrayOf(
				redContribution,
				greenContribution,
				blueContribution,
				0f,
				0f,
				redContribution,
				greenContribution,
				blueContribution,
				0f,
				0f,
				redContribution,
				greenContribution,
				blueContribution,
				0f,
				0f,
				0f,
				0f,
				0f,
				alphaUnchanged,
				0f
			)
		)
	}
}
