package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .verticalScroll(contentScrollState)
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
    Column(modifier = modifier) {

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

        PhoneAddressesBlock(
            state = screenState.addressesBlockState
        )

        VerticalSpacer(height = 16.dp)

        FwdSectionHeader(
            title = "Text filters:",
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(4.dp)

        FiltersBlock(
            state = ForwardingRuleEditorScreenState.FiltersBlockState(
                filters = emptyList(),
                onAddNewFilterRequest = {}
            ) // stub
        )

        Spacer(32.dp)
    }
}

@Composable
private fun PhoneAddressesBlock(
    state: ForwardingRuleEditorScreenState.AddressesBlockState
) {
    val shouldShowAddAddressDialog = remember { mutableStateOf(false) }
    val shouldShowAddFromKnownDialog = remember { mutableStateOf(false) }

    val onAddNewAddressClicked = remember {
        { shouldShowAddAddressDialog.value = true }
    }
    val onAddFromKnownClicked = remember {
        { shouldShowAddFromKnownDialog.value = true }
    }

    AnimatedContent(
        targetState = state.addresses,
        label = "address_block_content_anim"
    ) { addresses ->

        if (addresses.isNotEmpty()) {
            LazyColumn(
                userScrollEnabled = false,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                items(addresses) {  address ->
                    ForwardingRuleEditorPhoneAddressEntry(
                        phoneAddress = address,
                        onRemovePhoneAddress = { state.onRemoveAddressRequest(it) }
                    )
                }

                item {
                    AddPhoneAddressButton(
                        onAddAddressClicked = onAddNewAddressClicked,
                        onAddFromKnownClicked = onAddFromKnownClicked
                    )
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                EmptyAddressesPlaceholder(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                AddPhoneAddressButton(
                    onAddAddressClicked = onAddNewAddressClicked,
                    onAddFromKnownClicked = onAddFromKnownClicked
                )
            }
        }
    }

    ForwardingRuleEditorAddPhoneAddressDialog(isShown = shouldShowAddAddressDialog)
}

@Composable
private fun AddPhoneAddressButton(
    onAddAddressClicked: () -> Unit,
    onAddFromKnownClicked: () -> Unit
) {
    VerticalSpacer(height = 8.dp)

    ForwardingRuleEditorAddPhoneAddressButton(
        onAddNewAddressClicked = onAddAddressClicked,
        onAddFromKnownClicked = onAddFromKnownClicked
    )

    VerticalSpacer(height = 8.dp)
}

@Composable
private fun FiltersBlock(
    state: ForwardingRuleEditorScreenState.FiltersBlockState
) {
    AnimatedContent(
        targetState = state.filters,
        label = "filters_block_content_anim"
    ) { filters ->

        if (filters.isNotEmpty()) {
            Text("stub")
        } else {
            EmptyFiltersPlaceholder(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * This placeholder displays in the "Apply to numbers:" section
 * when there are no added addresses.
 */
@Composable
private fun EmptyAddressesPlaceholder(
    modifier: Modifier = Modifier
) {
    val placeholderText = remember {
        """
            The forwarding rule would be applied only to the phone numbers you add in this list.
            
            Add a number by typing it out by hand by clicking the "Add number" button, or choose from the known numbers from your message history or contact list by clicking "Add from known" button.
        """.trimIndent()
    }

    EmptyContentPlaceholder(text = placeholderText, modifier = modifier)
}

@Composable
private fun EmptyFiltersPlaceholder(
    modifier: Modifier = Modifier
) {
    val placeholderText = remember {
        """
            Adding text filters for the rule will allow the message to be forwarded only if its contents pass the added filters. If the rule has no filters, every message received from the numbers the rule is applied to would be forwarded. 
            
            You can add filters to the rule by pressing the button below.
        """.trimIndent()
    }

    EmptyContentPlaceholder(text = placeholderText, modifier = modifier)
}

@Composable
private fun EmptyContentPlaceholder(
    text: String,
    modifier: Modifier = Modifier
) {
    FwdDefaultPresetCard(
        elevation = 1.dp,
        disableShadow = true,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
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

    val addresses = listOf(
        "+79425174237",
        "+79135849638",
        "900",
        "SomeCompany"
    )
    val addressesEmpty = emptyList<String>()

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
            addresses = addressesEmpty,
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

@Preview
@Composable
private fun EmptyAddressesPlaceholder_Preview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            EmptyAddressesPlaceholder()
        }
    }
}
