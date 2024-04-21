package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ForwardingSettingsViewModel

/**
 * Forwarding settings screen main composable
 *
 * @param           onBackPressed Called when the user presses the back button,
 *                                should return to the previous screen.
 * @param onRuleEditorOpenRequest Called when the user requested to add a new rule or edit
 *                                an existing one. Should lead to the rule editor screen.
 *                                When the user wants to edit an existing rule, its ID
 *                                should be passed into this lambda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardingSettingsScreen(
    onBackPressed: () -> Unit,
    onRuleEditorOpenRequest: (existingRuleID: String?) -> Unit,
    viewModel: ForwardingSettingsViewModel = koinViewModel()
) {
    val currentBotUrl by viewModel.botUrlStateFlow.collectAsStateWithLifecycle()
    val currentSenderKey by viewModel.senderKeyStateFlow.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon =  {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ForwardingSettingsLayout(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 8.dp,
                start = 16.dp,
                end = 16.dp
            ),
            currentBotUrl = currentBotUrl,
            currentSenderKey = currentSenderKey,
            onCommitBotUrlUpdate = { newBotUrl -> viewModel.updateBotUrl(newBotUrl) },
            onCommitSenderKeyUpdate = { newSenderKey -> viewModel.updateSenderKey(newSenderKey) },
            onAddNewRuleRequest = { onRuleEditorOpenRequest(null) },
            onEditRuleRequest = { ruleID -> onRuleEditorOpenRequest(ruleID) }
        )
    }
}
