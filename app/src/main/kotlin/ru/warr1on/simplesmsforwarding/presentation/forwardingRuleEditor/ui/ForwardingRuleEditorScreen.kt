package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions.*
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState.*
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorViewModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorAddPhoneAddressButton
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorAddPhoneAddressDialog
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorPhoneAddressEntry
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components.ForwardingRuleEditorTextField

//region Navigation

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

//endregion

//region Main composable and layout

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
    val actions by viewModel.actions.collectAsStateWithLifecycle()

    ForwardingRuleEditorLayout(
        screenState = screenState,
        actions = actions,
        onCompletion = onCompletion
    )
}

@Composable
private fun ForwardingRuleEditorLayout(
    screenState: ForwardingRuleEditorScreenState,
    actions: ForwardingRuleEditorScreenActions,
    onCompletion: () -> Unit
) {
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
            actions = actions,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    ForwardingRuleEditorAddPhoneAddressDialog(
        state = screenState.addNewAddressDialogState,
        actions = actions.addNewAddressesDialogActions
    )
}

@Composable
private fun ForwardingRuleEditor(
    screenState: ForwardingRuleEditorScreenState,
    actions: ForwardingRuleEditorScreenActions,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {

        verticalSpacer(8.dp, key = "topmost_spacer".hashCode())

        textFieldsComponent(
            ruleNameTextFieldState = screenState.ruleNameTextFieldState,
            ruleNameTextFieldActions = actions.ruleNameTextFieldActions,
            messageTypeKeyTextFieldState = screenState.messageTypeTextFieldState,
            messageTypeKeyTextFieldActions = actions.messageTypeKeyTextFieldActions
        )

        sectionHeader("Apply to numbers:")

        phoneAddressesComponent(
            state = screenState.addressesBlockState,
            actions = actions.addressesComponentActions
        )

        sectionHeader("Text filters:")

        filtersComponent(
            state = screenState.filtersBlockState,
            actions = actions.filtersComponentActions
        )

        verticalSpacer(8.dp, key = "bottommost_spacer".hashCode())
    }
}

//endregion

//region Rule editor UI components

private fun LazyListScope.sectionHeader(
    headerTitleText: String
) {
    item(key = "section_header#$headerTitleText".hashCode()) {
        VerticalSpacer(height = 16.dp)

        FwdSectionHeader(
            title = headerTitleText,
            modifier = Modifier.padding(start = 16.dp)
        )

        VerticalSpacer(4.dp)
    }
}

private fun LazyListScope.textFieldsComponent(
    ruleNameTextFieldState: TextFieldState,
    ruleNameTextFieldActions: TextFieldActions,
    messageTypeKeyTextFieldState: TextFieldState,
    messageTypeKeyTextFieldActions: TextFieldActions
) {
    item(key = "rule_name_text_field_section".hashCode()) {
        ForwardingRuleEditorTextField(
            headerText = "Rule name",
            state = ruleNameTextFieldState,
            actions = ruleNameTextFieldActions,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    item(key = "msgtype_key_text_field_section".hashCode()) {
        ForwardingRuleEditorTextField(
            headerText = "Message type key",
            state = messageTypeKeyTextFieldState,
            actions = messageTypeKeyTextFieldActions,
            capitalization = KeyboardCapitalization.None,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

private fun LazyListScope.phoneAddressesComponent(
    state: AddressesBlockState,
    actions: AddressesComponentActions
) {
    if (state.addresses.isEmpty()) {
        item(key = "empty_addresses_placeholder".hashCode()) {
            EmptyAddressesPlaceholder(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    items(
        items = state.addresses,
        key = { "added_address#$it" }
    ) { address ->
        ForwardingRuleEditorPhoneAddressEntry(
            phoneAddress = address,
            onRemovePhoneAddress = actions.onRemoveAddressRequest,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    item(key = "add_address_button".hashCode()) {
        AddPhoneAddressButton(
            onAddAddressClicked = actions.onAddNewAddressRequest,
            onAddFromKnownClicked = actions.onAddFromKnownRequest
        )
    }
}

private fun LazyListScope.filtersComponent(
    state: FiltersBlockState,
    actions: FiltersComponentActions
) {
    if (state.filters.isEmpty()) {
        item(key = "no_filters_placeholder".hashCode()) {
            EmptyFiltersPlaceholder(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun AddPhoneAddressButton(
    onAddAddressClicked: () -> Unit,
    onAddFromKnownClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalSpacer(height = 8.dp)

    Row {
        Spacer()
        ForwardingRuleEditorAddPhoneAddressButton(
            onAddNewAddressClicked = onAddAddressClicked,
            onAddFromKnownClicked = onAddFromKnownClicked,
            modifier = modifier
        )
        Spacer()
    }

    VerticalSpacer(height = 8.dp)
}

//endregion

//region Empty content placeholders

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

//endregion

//region Previews

@Preview
@Composable
private fun ForwardingRuleEditorScreen_Preview() {

    val state = remember { generatePreviewScreenState() }
    val actions = remember { generatePreviewScreenActions() }

    AppTheme {
        ModalHostExperimental {
            ForwardingRuleEditorLayout(
                screenState = state,
                actions = actions,
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

    val ruleNameTextFieldState = TextFieldState(
        text = "Some rule name",
        isError = false,
        supportingText = ""
    )

    val messageTypeKeyTextFieldState = TextFieldState(
        text = "some_msgtype_key",
        isError = false,
        supportingText = ""
    )

    val addressesBlockState = AddressesBlockState(
        addresses = addresses
    )

    val filtersBlockState = FiltersBlockState(
        filters = emptyList()
    )

    val addNewAddressDialogState = AddNewAddressDialogState.NotShowing

    return ForwardingRuleEditorScreenState(
        screenTitle = "Rule editor",
        ruleNameTextFieldState = ruleNameTextFieldState,
        messageTypeTextFieldState = messageTypeKeyTextFieldState,
        addressesBlockState = addressesBlockState,
        filtersBlockState = filtersBlockState,
        addNewAddressDialogState = addNewAddressDialogState
    )
}

private fun generatePreviewScreenActions(): ForwardingRuleEditorScreenActions {
     return ForwardingRuleEditorScreenActions(
         ruleNameTextFieldActions = TextFieldActions {  },
         messageTypeKeyTextFieldActions = TextFieldActions {  },
         addressesComponentActions = AddressesComponentActions({}, {}, {}),
         filtersComponentActions = FiltersComponentActions({}, {}),
         addNewAddressesDialogActions = AddNewAddressDialogActions({}, {}, {})
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

//endregion
