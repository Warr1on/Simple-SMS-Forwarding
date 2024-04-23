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
 * in which case the spacer will try to take up as much available space as it can
 *
 * @param height Height of the spacer in DP. If the height is not specified,
 * the spacer would try to take up all available space.
 * @param modifier An optional modifier for the spacer
 */
@Composable
fun ColumnScope.Spacer(
    height: Dp? = null,
    modifier: Modifier = Modifier
) {
    VerticalSpacer(height, modifier)
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
fun RowScope.Spacer(
    width: Dp? = null,
    modifier: Modifier = Modifier
) {
    HorizontalSpacer(width, modifier)
}
