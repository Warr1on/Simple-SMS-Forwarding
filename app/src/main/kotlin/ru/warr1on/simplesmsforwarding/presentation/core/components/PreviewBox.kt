package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * The box used in most previews to put the preview content into.
 * By default has a `background` color and 16 dp padding, which
 * can be overridden by providing different [backgroundColor] and
 * [contentPadding] values.
 *
 * @param modifier An optional modifier to further setup and/or alter the box
 * @param backgroundColor The background color of the box
 * @param contentPadding Padding between the [content] and the box's bounds
 * @param content Preview content
 */
@Composable
fun PreviewBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(backgroundColor)
            .padding(contentPadding)
    ) {
        content()
    }
}


//--- Previews ---//

@Preview
@Composable
private fun PreviewBox_Preview() {
    AppTheme {
        PreviewBox {
            Text(text = "Example content")
        }
    }
}
