package com.amonga.snifflicks.core.compose.utility.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

fun <T> T?.applyLambdaIfNotNull(
	onNotNull: (T) -> @Composable () -> Unit
): @Composable (() -> Unit)? {
	return if (this != null) {
		onNotNull.invoke(this)
	} else null
}

@Composable
@ReadOnlyComposable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun LazyListState.getLastCompletelyVisibleItemIndex(): Int {
	return layoutInfo.visibleItemsInfo.lastOrNull {
		val viewportEndOffset = layoutInfo.viewportEndOffset
		it.offset + it.size <= viewportEndOffset
	}?.index ?: -1
}

val zeroDimen = 0.dp
