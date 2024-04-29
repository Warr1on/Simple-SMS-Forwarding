package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.verticalSpacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components.ForwarderSettingHeader
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components.ForwarderSettingTextFieldBlock
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components.ForwardingRuleAddButton
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components.ForwardingRuleCompactView

@Composable
fun ForwardingSettingsLayout(
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
    currentBotUrl: String,
    currentSenderKey: String,
    onCommitBotUrlUpdate: (newBotUrl: String) -> Unit,
    onCommitSenderKeyUpdate: (newSenderKey: String) -> Unit,
    onAddNewRuleRequest: () -> Unit,
    onEditRuleRequest: (ruleID: String) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        item {
            ForwarderSettingTextFieldBlock(
                settingName = "Backend URL",
                currentValue = currentBotUrl,
                onCommitNewValue = { newValue -> onCommitBotUrlUpdate(newValue) }
            )
        }

        verticalSpacer(16.dp)

        item {
            ForwarderSettingTextFieldBlock(
                settingName = "Sender key",
                currentValue = currentSenderKey,
                onCommitNewValue = { newValue -> onCommitSenderKeyUpdate(newValue) }
            )
        }

        item {
            ForwarderSettingHeader(
                title = "Forwarding rules",
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
        }

        item {
            ForwardingRuleCompactView(
                rule = PresentationModel.ForwardingRule(
                    id = "",
                    name = "Rule name",
                    typeKey = "msg_type_key",
                    applicableAddresses = listOf("+79452415764", "+79137748994", "900", "SomeCompany"),
                    filters = listOf(
                        PresentationModel.ForwardingFilter(
                            id = "",
                            filterType = PresentationModel.ForwardingFilter.FilterType.INCLUDE,
                            text = "Some SMS text that should be included",
                            ignoreCase = false
                        ),
                        PresentationModel.ForwardingFilter(
                            id = "",
                            filterType = PresentationModel.ForwardingFilter.FilterType.EXCLUDE,
                            text = "Some SMS text that should be excluded",
                            ignoreCase = true
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
                    .clickable {
                        onEditRuleRequest("some_rule_id_for_testing")
                    }
            )
        }

        verticalSpacer(16.dp)

        item {
            ForwardingRuleCompactView(
                rule = PresentationModel.ForwardingRule(
                    id = "",
                    name = "Rule name",
                    typeKey = "msg_type_key",
                    applicableAddresses = listOf("+79452415764", "+79137748994", "900", "SomeCompany"),
                    filters = listOf(
                        PresentationModel.ForwardingFilter(
                            id = "",
                            filterType = PresentationModel.ForwardingFilter.FilterType.INCLUDE,
                            text = "Some SMS text that should be included",
                            ignoreCase = false
                        ),
                        PresentationModel.ForwardingFilter(
                            id = "",
                            filterType = PresentationModel.ForwardingFilter.FilterType.EXCLUDE,
                            text = "Some SMS text that should be excluded",
                            ignoreCase = true
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        verticalSpacer(16.dp)

        item {
            ForwardingRuleAddButton(
                onClick = onAddNewRuleRequest
            )
        }
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingSettingsLayout_Preview() {
    AppTheme {
        ForwardingSettingsLayout(
            currentBotUrl = "https://test.com/fwdbot",
            currentSenderKey = "my_sender_key",
            onCommitBotUrlUpdate = { _ -> },
            onCommitSenderKeyUpdate = { _ -> },
            onAddNewRuleRequest = {},
            onEditRuleRequest = { _ -> }
        )
    }
}
