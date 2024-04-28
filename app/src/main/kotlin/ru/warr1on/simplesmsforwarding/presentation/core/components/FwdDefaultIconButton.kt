package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A general-purpose icon button.
 * This variant accepts [Painter] as a parameter for the icon
 *
 * @param painter Icon's painter
 * @param contentDescription Icon button content description for accessibility
 * @param onClick Called when the icon button is clicked
 * @param modifier Modifier for the icon button
 * @param color Color that will be applied as a tint for the icon. If null,
 * the icon will use the color from the [LocalContentColor]
 */
@Composable
fun FwdDefaultIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null
) {
    val resolvedColor = resolvedColor(color)

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(resolvedColor),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * A general-purpose icon button.
 * This variant accepts [ImageVector] as a parameter for the icon
 *
 * @param imageVector Icon's image vector
 * @param contentDescription Icon button content description for accessibility
 * @param onClick Called when the icon button is clicked
 * @param modifier Modifier for the icon button
 * @param color Color that will be applied as a tint for the icon. If null,
 * the icon will use the color from the [LocalContentColor]
 */
@Composable
fun FwdDefaultIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null
) {
    val resolvedColor = resolvedColor(color)

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = resolvedColor
        )
    }
}

/**
 * Returns either the color provided in the parameter if
 * it's not null, or the color from the [LocalContentColor]
 */
@Composable
private fun resolvedColor(color: Color?): Color {
    val localContentColor = LocalContentColor.current
    return when (color) {
        null -> localContentColor
        else -> color
    }
}


//--- Previews ---//

@Preview
@Composable
private fun FwdDefaultIconButton_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)) {
            FwdDefaultIconButton(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Go back",
                onClick = {}
            )
        }
    }
}
