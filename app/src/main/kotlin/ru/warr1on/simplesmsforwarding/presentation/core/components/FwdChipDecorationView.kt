package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A simple decorative container with text that looks similar to a Material chip
 */
@Composable
fun FwdChipDecorationView(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp, 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun FwdChipDecorationView_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)) {
            FwdChipDecorationView(text = "Some value")
        }
    }
}
