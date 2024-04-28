package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

//region General purpose spacers

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

//endregion

//region Row and column spacers

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

//endregion

//region Lazy list spacers

/**
 * Creates a vertical spacer wrapped in a lazy list's `item`
 *
 * @param height Height of the spacer in DP
 * @param key A stable and unique key representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param modifier An optional modifier for the spacer composable
 */
fun LazyListScope.verticalSpacer(
    height: Dp,
    key: Any? = null,
    modifier: Modifier = Modifier
) {
    item(key = key) {
        Spacer(modifier.height(height))
    }
}

/**
 * Creates a horizontal spacer wrapped in a lazy list's `item`
 *
 * @param width Width of the spacer in DP
 * @param key A stable and unique key representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param modifier An optional modifier for the spacer composable
 */
fun LazyListScope.horizontalSpacer(
    width: Dp,
    key: Any? = null,
    modifier: Modifier = Modifier
) {
    item(key = key) {
        Spacer(modifier.width(width))
    }
}

//endregion
