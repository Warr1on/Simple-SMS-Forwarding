package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A general-purpose card container with the default forwarder app preset.
 *
 * @param modifier The card container's modifier
 * @param shape Card shape
 * @param elevation Card elevation. Determines the shadow and surface color intensity
 * @param disableShadow Determines if the card should have a shadow. If false, the shadow
 * would be drawn normally; if true, the card container would be clipped to its shape, and
 * the outer shadow wouldn't be visible because of that. False by default.
 * @param useOutlineForDarkTheme Determines if the card should have an outline in the dark mode.
 * True by default.
 * @param contentPadding The padding between the card container and its contents
 * @param content The card's content
 */
@Composable
fun FwdDefaultPresetCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: Dp = 2.dp,
    disableShadow: Boolean = false,
    useOutlineForDarkTheme: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    val cardBorder: BorderStroke? = if (isSystemInDarkTheme()) {
        when (useOutlineForDarkTheme) {
            true -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
            false -> null
        }
    } else {
        null
    }

    val cardColors = if (!isSystemInDarkTheme()) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    } else {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    val cardElevation: CardElevation = if (!isSystemInDarkTheme()) {
        CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    } else {
        CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    }

    val cardModifier = when (disableShadow) {
        false -> modifier
        true -> modifier.clip(shape)
    }

    Card(
        shape = shape,
        border = cardBorder,
        colors = cardColors,
        elevation = cardElevation,
        modifier = cardModifier
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Preview
@Composable
private fun FwdDefaultPresetCard_Preview() {
    AppTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            FwdDefaultPresetCard(
                contentPadding = PaddingValues(12.dp)
            ) {
                Text("Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio. Exercitationem ducimus facere dolores.")
            }
        }
    }
}
