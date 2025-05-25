package com.amonga.snifflicks.core.compose.utility.extensions

import android.annotation.SuppressLint
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import com.amonga.snifflicks.core.compose.utility.eventthrottle.EventsThrottler
import com.amonga.snifflicks.core.compose.utility.eventthrottle.get

inline fun <T : Any?> Modifier.applyWhenNotNull(
    data: T?,
    block: Modifier.(t: T) -> Modifier,
): Modifier {
    return if (data != null) {
        val modifier = Modifier
        then(modifier.block(data))
    } else {
        this
    }
}

inline fun Modifier.applyWhenTrue(
    predicate: Boolean,
    block: Modifier.() -> Modifier,
): Modifier {
    return if (predicate) {
        val modifier = Modifier
        then(modifier.block())
    } else {
        this
    }
}

fun Modifier.throttledClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    showRipple: Boolean = false,
    throttlerTimeout: Long = 1000L,
    onClickThrottled: ((Long) -> Unit)? = null,
    onClick: () -> Unit
) = composed(inspectorInfo = debugInspectorInfo {
    name = "clickable"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["role"] = role
    properties["onClickThrottled"] = onClickThrottled
    properties["onClick"] = onClick
}) {
    val throttler = remember { EventsThrottler.get(throttlerTimeout) }
    this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { throttler.processEvent(throttledEvent = onClickThrottled) { onClick() } },
        role = role,
        indication = if (showRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() })
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.snippetClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    showRipple: Boolean? = false,
    throttle: Boolean? = false,
    onClick: () -> Unit
) = composed(inspectorInfo = debugInspectorInfo {
    name = "clickable"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["role"] = role
    properties["onClick"] = onClick
}) {
    val throttler = remember { EventsThrottler.get() }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { if (throttle == true) throttler.processEvent { onClick() } else onClick() },
        role = role,
        indication = if (showRipple == true) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() })
}

fun Modifier.conditional(
    condition: Boolean, ifModifier: Modifier, elseModifier: Modifier? = null
): Modifier {
    return if (condition) {
        then(ifModifier)
    } else if (elseModifier != null) {
        then(elseModifier)
    } else {
        this
    }
}
