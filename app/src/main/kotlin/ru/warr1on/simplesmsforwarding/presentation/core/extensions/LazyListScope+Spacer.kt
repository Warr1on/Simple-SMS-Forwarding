package ru.warr1on.simplesmsforwarding.presentation.core.extensions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Helper extension functions for adding spacers into LazyRow and LazyColumn

/**
 * Adds a [Spacer] with the provided [width] and [height] as an item into the lazy list
 *
 * @param width Spacer width
 * @param height Spacer height
 */
fun LazyListScope.spacer(width: Dp = 0.dp, height: Dp = 0.dp) {
    item {
        Spacer(modifier = Modifier.size(width = width, height = height))
    }
}

/**
 * Adds a vertical [Spacer] with the provided [height] as an item into the lazy list.
 * Useful for spacing out column sections in LazyColumn
 *
 * @param height Spacer height
 */
fun LazyListScope.verticalSpacer(height: Dp) = spacer(height = height)

/**
 * Adds a horizontal [Spacer] with the provided [width] as an item into the lazy list.
 * Useful for spacing out row sections in LazyRow
 *
 * @param width Spacer width
 * @param height Spacer height
 */
fun LazyListScope.horizontalSpacer(width: Dp) = spacer(width = width)
