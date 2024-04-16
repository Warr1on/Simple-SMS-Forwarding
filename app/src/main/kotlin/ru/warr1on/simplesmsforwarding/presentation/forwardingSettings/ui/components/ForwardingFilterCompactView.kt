package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.model.ForwardingSettingsScreenModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.model.ForwardingSettingsScreenModel.ForwardingFilter.FilterType.*

@Composable
fun ForwardingFilterCompactView(
    filter: ForwardingSettingsScreenModel.ForwardingFilter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp, 8.dp)
    ) {
        val filterTypeText = when (filter.filterType) {
            INCLUDE -> "Includes:"
            EXCLUDE -> "Excludes:"
        }
        val ignoresCaseText = when (filter.ignoreCase) {
            true -> "(Ignoring case)"
            false -> "(Respecting case)"
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = filterTypeText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = ignoresCaseText,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = filter.text
        )
    }
}

@Preview
@Composable
private fun ForwardingFilterCompactView_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            ForwardingFilterCompactView(
                filter = ForwardingSettingsScreenModel.ForwardingFilter(
                    id = "",
                    filterType = INCLUDE,
                    text = "Some SMS text that should be included",
                    ignoreCase = false
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
