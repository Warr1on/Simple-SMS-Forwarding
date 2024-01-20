package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingSettingsLayout(
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        item {
            ForwardingBackendEndpointSettingBlock()
        }

        item {
            FwdSettingHeader(
                title = "Forwarding rules",
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
        }

        item {
            ForwardingRuleCompactView(Modifier.fillMaxWidth())
        }

        item {
            Spacer(Modifier.height(16.dp))
        }

        item {
            ForwardingRuleCompactView(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ForwardingBackendEndpointSettingBlock() {
    Column(Modifier.fillMaxWidth()) {

        FwdSettingHeader(title = "Backend endpoint")

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = "https://test.com/fwdbot",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ForwardingRuleCompactView(
    modifier: Modifier = Modifier
) {
    FwdDefaultPresetCard(
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        Column {

            Text(
                text = "Rule name",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Applied to:",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary
            )

            FlowRow {
                SuggestionChip(
                    onClick = {},
                    label = { Text("+79452415764") },
                    modifier = Modifier.padding(0.dp)
                )

                Spacer(Modifier.width(8.dp))

                SuggestionChip(
                    onClick = {},
                    label = { Text("+79137748994") },
                    modifier = Modifier.padding(0.dp)
                )

                Spacer(Modifier.width(8.dp))

                SuggestionChip(
                    onClick = {},
                    label = { Text("900") },
                    modifier = Modifier.padding(0.dp)
                )

                Spacer(Modifier.width(4.dp))

                SuggestionChip(
                    onClick = {},
                    label = { Text("SomeCompany") },
                    modifier = Modifier.padding(0.dp)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Text filters:",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(Modifier.height(8.dp))

            ForwardingFilterCompactView(
                filterType = FilterType.INCLUDE,
                filterText = "Some SMS text that should be included",
                ignoresCase = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            ForwardingFilterCompactView(
                filterType = FilterType.EXCLUDE,
                filterText = "Some SMS text that should be excluded",
                ignoresCase = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ForwardingFilterCompactView(
    filterType: FilterType,
    filterText: String,
    ignoresCase: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
//            .clip(RoundedCornerShape(12.dp))
//            .background(MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            .padding(12.dp, 8.dp)
    ) {
        val filterTypeText = when (filterType) {
            FilterType.INCLUDE -> "Includes:"
            FilterType.EXCLUDE -> "Excludes:"
        }
        val ignoresCaseText = when (ignoresCase) {
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
            text = filterText
        )
    }
}

@Composable
private fun FwdSettingHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Light,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 12.dp)
    )
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingSettingsLayout_Preview() {
    AppTheme {
        ForwardingSettingsLayout()
    }
}

@Preview
@Composable
private fun ForwardingBackendEndpointSettingBlock_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            ForwardingBackendEndpointSettingBlock()
        }
    }
}

@Preview
@Composable
private fun ForwardingRuleCompactView_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            ForwardingRuleCompactView(Modifier.fillMaxWidth())
        }
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
                filterType = FilterType.INCLUDE,
                filterText = "Some SMS text that should be included",
                ignoresCase = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
