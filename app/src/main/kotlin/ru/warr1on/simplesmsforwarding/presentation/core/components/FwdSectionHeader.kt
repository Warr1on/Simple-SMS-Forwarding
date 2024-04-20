package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * Default section header for the forwarder app
 *
 * @param title Section title
 * @param useDefaultHeaderPadding Determines if the section header should have default padding added to it
 * @param modifier Modifier for the section header
 */
@Composable
fun FwdSectionHeader(
    title: String,
    useDefaultHeaderPadding: Boolean = true,
    modifier: Modifier = Modifier
) {
    val headerModifier = when (useDefaultHeaderPadding) {
        true -> modifier.padding(start = 12.dp)
        false -> modifier
    }

    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Light,
        color = MaterialTheme.colorScheme.primary,
        modifier = headerModifier
    )
}

@Preview
@Composable
private fun FwdSectionHeader_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            FwdSectionHeader(title = "Setting Header")
        }
    }
}
