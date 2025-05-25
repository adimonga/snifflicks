package com.amonga.snifflicks.core.compose.utility.eventthrottle

import android.os.SystemClock

interface EventsThrottler {

	fun processEvent(throttledEvent: ((Long) -> Unit)? = null, event: () -> Unit)

	companion object
}

fun EventsThrottler.Companion.get(timeout: Long = 1000L): EventsThrottler = EventsThrottlerImpl(timeout)

private class EventsThrottlerImpl(val timeout: Long): EventsThrottler {

	private var lastEventTimeMs: Long = 0

	override fun processEvent(throttledEvent: ((Long) -> Unit)?, event: () -> Unit) {
		val now = SystemClock.elapsedRealtime()
		val currentTimeout = now - lastEventTimeMs
		if (currentTimeout >= timeout) {
			event.invoke()
		} else {
			throttledEvent?.invoke(timeout - currentTimeout)
		}
		lastEventTimeMs = now
	}
}
