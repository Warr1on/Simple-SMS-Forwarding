package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdChipDecorationView
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.model.ForwardingSettingsScreenModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ForwardingRuleCompactView(
    rule: ForwardingSettingsScreenModel.ForwardingRule,
    modifier: Modifier = Modifier
) {
    FwdDefaultPresetCard(
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = modifier
    ) {
        Column {

            Text(
                text = rule.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Text(
                text = "Message type key: ${rule.typeKey}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Applied to:",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                rule.applicableAddresses.forEach { address ->
                    FwdChipDecorationView(
                        text = address,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Text filters:",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 12.dp)
            )

            rule.filters.forEach { filter ->
                ForwardingFilterCompactView(
                    filter = filter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingRuleCompactView_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            ForwardingRuleCompactView(
                rule = ForwardingSettingsScreenModel.ForwardingRule(
                    id = "",
                    name = "Rule name",
                    typeKey = "msg_type_key",
                    applicableAddresses = listOf("+79452415764", "+79137748994", "900", "SomeCompany"),
                    filters = listOf(
                        ForwardingSettingsScreenModel.ForwardingFilter(
                            id = "",
                            filterType = ForwardingSettingsScreenModel.ForwardingFilter.FilterType.INCLUDE,
                            text = "Some SMS text that should be included",
                            ignoreCase = false
                        ),
                        ForwardingSettingsScreenModel.ForwardingFilter(
                            id = "",
                            filterType = ForwardingSettingsScreenModel.ForwardingFilter.FilterType.EXCLUDE,
                            text = "Some SMS text that should be excluded",
                            ignoreCase = true
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
