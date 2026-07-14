package com.dosemate.android.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.MutableTransitionState

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Context extensions.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Modifier extensions.
 */
fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        onClick = onClick
    )
}

/**
 * Animates a component's appearance with a gentle fade and scale-up effect.
 */
fun Modifier.fadeInScale(delay: Int = 0): Modifier = composed {
    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val transition = updateTransition(transitionState, label = "fadeInScale")
    
    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500, delayMillis = delay) },
        label = "alpha"
    ) { if (it) 1f else 0f }
    
    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 400, delayMillis = delay) },
        label = "scale"
    ) { if (it) 1f else 0.95f }

    this
        .alpha(alpha)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}

