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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun FwdDefaultIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun FwdDefaultIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun FwdDefaultIconButton_Preview() {
    AppTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            FwdDefaultIconButton(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Go back",
                onClick = {}
            )
        }
    }
}
