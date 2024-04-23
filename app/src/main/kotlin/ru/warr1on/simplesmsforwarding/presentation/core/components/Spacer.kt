package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Creates a vertical spacer.
 *
 * You can either specify the exact [height] of the spacer, or leave the field empty,
 * in which case the spacer will try to take up as much available space as it can
 *
 * @param height Height of the spacer in DP. If the height is not specified,
 * the spacer would try to take up all available space.
 * @param modifier An optional modifier for the spacer
 */
@Composable
fun VerticalSpacer(
    height: Dp? = null,
    modifier: Modifier = Modifier
) {
    val spacerModifier = when (height) {
        null -> modifier.fillMaxHeight()
        else -> modifier.height(height)
    }

    Spacer(spacerModifier)
}

/**
 * Creates a horizontal spacer.
 *
 * You can either specify the exact [width] of the spacer, or leave the field empty,
 * in which case the spacer will try to take up as much available space as it can
 *
 * @param width Width of the spacer in DP. If the width is not specified,
 * the spacer would try to take up all available space
 * @param modifier An optional modifier for the spacer
 */
@Composable
fun HorizontalSpacer(
    width: Dp? = null,
    modifier: Modifier = Modifier
) {
    val spacerModifier = when (width) {
        null -> modifier.fillMaxWidth()
        else -> modifier.width(width)
    }

    Spacer(spacerModifier)
}

/**
 * Creates a vertical spacer.
 *
 * You can either specify the exact [height] of the spacer, or leave the field empty,
 * in which case the spacer will try to take up as much available space as it can, but
 * without encroaching on the other composables due to having low default layout weight
 * of 0.1 (which can be overridden by providing a [weight] param)
 *
 * @param height Height of the spacer in DP. If the height is not specified,
 * the spacer would try to take up all available space.
 * @param weight Layout weight of the spacer, 0.1 by default. Has no effect if the [height]
 * value is provided
 * @param modifier An optional modifier for the spacer
 */
@Composable
fun ColumnScope.Spacer(
    height: Dp? = null,
    weight: Float = 0.1f,
    modifier: Modifier = Modifier
) {
    val spacerModifier = when (height) {
        null -> modifier.fillMaxHeight().weight(weight)
        else -> modifier.height(height)
    }

    Spacer(spacerModifier)
}

/**
 * Creates a horizontal spacer.
 *
 * You can either specify the exact [width] of the spacer, or leave the field empty,
 * in which case the spacer will try to take up as much available space as it can, but
 * without encroaching on the other composables due to having low default layout weight
 * of 0.1 (which can be overridden by providing a [weight] param)
 *
 * @param width Width of the spacer in DP. If the width is not specified,
 * the spacer would try to take up all available space
 * @param weight Layout weight of the spacer, 0.1 by default. Has no effect if the [width]
 * value is provided
 * @param modifier An optional modifier for the spacer
 */
@Composable
fun RowScope.Spacer(
    width: Dp? = null,
    weight: Float = 0.1f,
    modifier: Modifier = Modifier
) {
    val spacerModifier = when (width) {
        null -> modifier.fillMaxWidth().weight(weight)
        else -> modifier.width(width)
    }

    Spacer(spacerModifier)
}
