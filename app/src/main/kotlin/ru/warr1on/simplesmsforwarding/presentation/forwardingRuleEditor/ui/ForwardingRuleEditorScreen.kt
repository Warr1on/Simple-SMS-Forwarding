package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.warr1on.simplesmsforwarding.presentation.core.components.*
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalHostExperimental
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorViewModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model.ForwardingRuleEditorScreenState
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorAddPhoneAddressButton
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorAddPhoneAddressDialog
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorPhoneAddressEntry
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorTextField

/**
 * Nav graph destination for the forwarding rule editor screen
 *
 * @param onCompletion Called when the user has finished editing (either by creating
 * a rule, modifying and saving an existing one, or closing the screen without saving)
 */
fun NavGraphBuilder.forwardingRuleEditorScreen(
    onCompletion: () -> Unit
) {
    composable(
        route = "fwd-rule-editor?ruleId={ruleId}",
        enterTransition = { fadeIn() + slideInVertically { it/2 } },
        exitTransition = { fadeOut() + slideOutVertically { it/2 } },
    ) {
        ForwardingRuleEditorScreen(onCompletion = onCompletion)
    }
}

/**
 * Forwarding rule editor screen main composable
 *
 * @param onCompletion Called when the user has finished editing (either by creating
 * a rule, modifying and saving an existing one, or closing the screen without saving)
 * @param viewModel [ForwardingRuleEditorViewModel] instance for this screen
 */
@Composable
fun ForwardingRuleEditorScreen(
    onCompletion: () -> Unit,
    viewModel: ForwardingRuleEditorViewModel = koinViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    ForwardingRuleEditorLayout(
        screenState = screenState,
        onCompletion = onCompletion
    )
}

@Composable
private fun ForwardingRuleEditorLayout(
    screenState: ForwardingRuleEditorScreenState,
    onCompletion: () -> Unit
) {
    val contentScrollState = rememberScrollState()

    FwdDefaultTopAppBarScaffold(
        navbarTitle = screenState.screenTitle,
        navigationIcon = {
            FwdDefaultIconButton(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close rule editor",
                onClick = { onCompletion() }
            )
        }
    ) { paddingValues ->
        ForwardingRuleEditor(
            screenState = screenState,
            modifier = Modifier
                .scrollable(contentScrollState, orientation = Orientation.Vertical)
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun ForwardingRuleEditor(
    screenState: ForwardingRuleEditorScreenState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(8.dp)

        ForwardingRuleEditorTextField(
            title = "Rule name",
            text = screenState.ruleNameTextFieldState.text,
            onValueChange = screenState.ruleNameTextFieldState.onTextChange,
            supportingText = screenState.ruleNameTextFieldState.supportingText,
            isError = screenState.ruleNameTextFieldState.isError,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ForwardingRuleEditorTextField(
            title = "Message type key",
            text = screenState.messageTypeTextFieldState.text,
            onValueChange = screenState.messageTypeTextFieldState.onTextChange,
            supportingText = screenState.messageTypeTextFieldState.supportingText,
            isError = screenState.messageTypeTextFieldState.isError,
            capitalization = KeyboardCapitalization.None,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        VerticalSpacer(height = 16.dp)

        FwdSectionHeader(
            title = "Apply to numbers:",
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(height = 4.dp)

        ForwardingRuleEditorPhoneAddressesBlock(
            phoneAddresses = ImmutableWrappedList.unsafeInit(
                listOf(
                    "+79425174237",
                    "+79135849638",
                    "900",
                    "SomeCompany"
                )
            ),
            onRemoveAddressRequest = {},
            onAddAddressRequest = {}
        )

        VerticalSpacer(height = 16.dp)

        FwdSectionHeader(
            title = "Text filters:",
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun ForwardingRuleEditorPhoneAddressesBlock(
    phoneAddresses: ImmutableWrappedList<String>,
    onRemoveAddressRequest: (phoneAddress: String) -> Unit,
    onAddAddressRequest: () -> Unit
) {
    val addresses = phoneAddresses()

    val shouldShowAddAddressDialog = remember { mutableStateOf(false) }

    LazyColumn(
        userScrollEnabled = false,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(addresses) { address ->
            ForwardingRuleEditorPhoneAddressEntry(
                phoneAddress = address,
                onRemovePhoneAddress = { onRemoveAddressRequest (it) }
            )
        }

        item {
            VerticalSpacer(height = 8.dp)

            ForwardingRuleEditorAddPhoneAddressButton(
                onAddNewAddressClicked = { shouldShowAddAddressDialog.value = true },
                onAddFromKnownClicked = {}
            )

            VerticalSpacer(height = 8.dp)
        }
    }

    ForwardingRuleEditorAddPhoneAddressDialog(isShown = shouldShowAddAddressDialog)
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingRuleEditorScreen_Preview() {

    val state = remember { generatePreviewScreenState() }

    AppTheme {
        ModalHostExperimental {
            ForwardingRuleEditorLayout(
                screenState = state,
                onCompletion = { }
            )
        }
    }
}

private fun generatePreviewScreenState(): ForwardingRuleEditorScreenState {

    val ruleNameTextFieldState = ForwardingRuleEditorScreenState.TextFieldState(
        text = "Some rule name",
        isError = false,
        supportingText = "",
        onTextChange = {}
    )

    val messageTypeKeyTextFieldState = ForwardingRuleEditorScreenState.TextFieldState(
        text = "some_msgtype_key",
        isError = false,
        supportingText = "",
        onTextChange = {}
    )

    val initialAddressesBlockState: () -> ForwardingRuleEditorScreenState.AddressesBlockState = {
        ForwardingRuleEditorScreenState.AddressesBlockState(
            addresses = emptyList(),
            modalState = ForwardingRuleEditorScreenState.AddressesBlockState.ModalState.NONE,
            onAddAddressRequest = {},
            onRemoveAddressRequest = {}
        )
    }

    return ForwardingRuleEditorScreenState(
        screenTitle = "Rule editor",
        ruleNameTextFieldState = ruleNameTextFieldState,
        messageTypeTextFieldState = messageTypeKeyTextFieldState,
        addressesBlockState = initialAddressesBlockState()
    )
}
