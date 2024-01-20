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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun FwdDefaultPresetCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    val cardBorder: BorderStroke? = if (isSystemInDarkTheme()) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
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
            defaultElevation = 2.dp
        )
    } else {
        CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        border = cardBorder,
        colors = cardColors,
        elevation = cardElevation,
        modifier = modifier
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
