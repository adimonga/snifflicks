package com.amonga.snifflicks.core.compose.utility.composeutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun DisposableEffectWithLifecycle(
	onCreate: () -> Unit = {},
	onStart: () -> Unit = {},
	onStop: () -> Unit = {},
	onResume: () -> Unit = {},
	onPause: () -> Unit = {},
	onDestroy: () -> Unit = {},
	onDispose: () -> Unit = {},
	lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
	vararg keys: Any?,
) {
	val currentOnCreate by rememberUpdatedState(onCreate)
	val currentOnStart by rememberUpdatedState(onStart)
	val currentOnStop by rememberUpdatedState(onStop)
	val currentOnResume by rememberUpdatedState(onResume)
	val currentOnPause by rememberUpdatedState(onPause)
	val currentOnDestroy by rememberUpdatedState(onDestroy)
	val currentOnDispose by rememberUpdatedState(onDispose)

	DisposableEffect(lifecycleOwner, *keys) {
		val lifecycleEventObserver = LifecycleEventObserver { _, event ->
			when (event) {
				Lifecycle.Event.ON_CREATE -> currentOnCreate()
				Lifecycle.Event.ON_START -> currentOnStart()
				Lifecycle.Event.ON_PAUSE -> currentOnPause()
				Lifecycle.Event.ON_RESUME -> currentOnResume()
				Lifecycle.Event.ON_STOP -> currentOnStop()
				Lifecycle.Event.ON_DESTROY -> currentOnDestroy()
				else -> {}
			}
		}
		lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

		onDispose {
			currentOnDispose.invoke()
			lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
		}
	}
}
