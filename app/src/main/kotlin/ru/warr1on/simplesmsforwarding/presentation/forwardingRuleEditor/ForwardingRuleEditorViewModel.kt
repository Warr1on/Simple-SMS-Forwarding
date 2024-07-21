package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions.*
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState.*
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel
import java.util.UUID

/**
 * A view model for the forwarding rule editor screen
 */
class ForwardingRuleEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val rulesRepo: ForwardingRulesRepository
) : ViewModel() {

    private val _screenState = MutableStateFlow(generateInitialScreenState())
    private val _actions = MutableStateFlow(generateActions())

    //region Public API

    /**
     * The current state of the forwarding rule editor screen
     */
    val screenState = _screenState.asStateFlow()

    /**
     * Actions to bind to various UI components
     */
    val actions = _actions.asStateFlow()

    //endregion

    //region Initialization and editor setup

    private var ruleID: String? = null

    init {
        // Saved state handle would return "null" string instead of the
        // actual null in the absence of value, but the default value lets
        // us to get rid of the optional here.
        val retrievedRuleID: String = savedStateHandle["ruleId"] ?: "null"
        // Now we can set an actual null for the ruleID, or the retrieved
        // value if it exists
        ruleID = when (retrievedRuleID) {
            "null" -> null
            else -> retrievedRuleID
        }
        // This piece of code looks really stupid, but oh well, what can you really
        // expect when having to deal with Google's garbage APIs ¯\_(ツ)_/¯
        // (The probable culprit of this weird behavior is most likely Navigation-Compose)

        ruleID?.let { setupEditorForEditingRuleWithID(it) }
    }

    private fun setupEditorForEditingRuleWithID(ruleID: String) {
        _screenState.update { it.copy(screenTitle = "Rule editor") }
        viewModelScope.launch {
            val rule = rulesRepo.getRule(ruleID)
            if (rule != null) {
                setupEditorAccordingToRule(rule)
            } else {

            }
        }
    }

    private fun setupEditorAccordingToRule(rule: ForwardingRule) {
        _screenState.update { previousState ->
            previousState.copy(
                ruleNameTextFieldState = previousState.ruleNameTextFieldState.copy(
                    text = rule.name
                ),
                messageTypeTextFieldState = previousState.messageTypeTextFieldState.copy(
                    text = rule.typeKey
                ),
                addressesBlockState = previousState.addressesBlockState.copy(
                    addresses = rule.applicableAddresses
                )
            )
        }
    }

    //endregion

    //region Initial state and actions generation

    private fun generateInitialScreenState(): ForwardingRuleEditorScreenState {
        return ForwardingRuleEditorScreenState(
            screenTitle = "New rule",
            ruleNameTextFieldState = emptyTextFieldState(),
            messageTypeTextFieldState = emptyTextFieldState(),
            addressesBlockState = AddressesBlockState(addresses = emptyList()),
            filtersBlockState = FiltersBlockState(filters = emptyList()),
            addNewAddressDialogState = AddNewAddressDialogState.NotDisplayed,
            filterEditorDialogState = FilterEditorDialogState.NotDisplayed
        )
    }

    private fun generateActions(): ForwardingRuleEditorScreenActions {

        val ruleNameTextFieldActions = TextFieldActions(
            onTextInputRequest = { proposedNewText -> onRuleNameTextChange(proposedNewText) }
        )

        val messageTypeKeyTextFieldActions = TextFieldActions(
            onTextInputRequest = { proposedNewText -> onMessageTypeTextChange(proposedNewText) }
        )

        val addressesComponentActions = AddressesComponentActions(
            onAddNewAddressRequest = { onOpenAddNewAddressDialogRequest() },
            onAddFromKnownRequest = {  }, //TODO
            onRemoveAddressRequest = { phoneAddress -> onRemoveAddressRequest(phoneAddress) }
        )

        val filtersComponentActions = FiltersComponentActions(
            onAddNewFilter = { onAddNewFilterRequest() },
            onRemoveFilter = { filterID -> onRemoveFilterRequest(filterID) }
        )

        val addNewAddressDialogActions = AddNewAddressDialogActions(
            onTextInputRequest = { proposedNewText -> onPhoneAddressInputChangeRequest(proposedNewText) },
            onAddNewAddressRequest = { addPhoneAddressFromNewPhoneAddressDialogInputs() },
            onDialogDismissed = { onAddNewAddressDialogDismissal() }
        )

        val filterEditorDialogActions = FilterEditorDialogActions(
            onFilterTypeSelectionChangeRequest = { newSelection -> onFilterEditorDialogFilterTypeSelectionChangeRequest(newSelection) },
            onIgnoresCaseSelectionChangeRequest = { newSelection -> onFilterEditorCaseToggleSwitchRequest(newSelection) },
            onTextInputRequest = { proposedNewText -> onFilterEditorDialogTextChangeRequest(proposedNewText) },
            onSaveFilterRequest = { addFilterFromFilterEditorDialogInputs() },
            onDialogDismissed = { onFilterEditorDialogDismissal() }
        )

        return ForwardingRuleEditorScreenActions(
            ruleNameTextFieldActions = ruleNameTextFieldActions,
            messageTypeKeyTextFieldActions = messageTypeKeyTextFieldActions,
            addressesComponentActions = addressesComponentActions,
            filtersComponentActions = filtersComponentActions,
            addNewAddressesDialogActions = addNewAddressDialogActions,
            filterEditorDialogActions = filterEditorDialogActions
        )
    }

    //endregion

    //region Behavior: rule name and message type key text field components

    /**
     * This will be called whenever the text in the rule name text field would be
     * requested to change. Here the proposed new text will be validated and the
     * new state of the corresponding text field would be generated.
     *
     * @param text The field input's proposed text.
     */
    private fun onRuleNameTextChange(text: String) {

        // Make sure that text isn't blank, otherwise signal error
        if (text.isBlank()) {
            _screenState.update { it.copy(
                ruleNameTextFieldState = it.ruleNameTextFieldState.copy(
                    text = text,
                    isError = true,
                    supportingText = "Text cannot be blank"
                )
            ) }
            return
        }
        // Make sure that text isn't over the character limit, otherwise signal error
        if (text.length > 30) {
            _screenState.update { it.copy(
                ruleNameTextFieldState = it.ruleNameTextFieldState.copy(
                    text = text,
                    isError = true,
                    supportingText = "Text exceeds the character limit"
                )
            ) }
            return
        }

        // If all the checks are passed, simply update the text in the text field
        _screenState.update { it.copy(
            ruleNameTextFieldState = it.ruleNameTextFieldState.copy(
                text = text,
                isError = false,
                supportingText = null
            )
        ) }
    }

    /**
     * This will be called whenever the text in the message type key text field
     * would be requested to change. Here the proposed new text will be validated
     * and the new state of the corresponding text field would be generated.
     *
     * @param text The field input's proposed text.
     */
    private fun onMessageTypeTextChange(text: String) {

        // Make sure that text isn't blank, otherwise signal error
        if (text.isBlank()) {
            _screenState.update { it.copy(
                messageTypeTextFieldState = it.messageTypeTextFieldState.copy(
                    text = text,
                    isError = true,
                    supportingText = "Text cannot be blank"
                )
            ) }
            return
        }
        // Make sure that text isn't over the character limit, otherwise signal error
        if (text.length > 30) {
            _screenState.update { it.copy(
                messageTypeTextFieldState = it.messageTypeTextFieldState.copy(
                    text = text,
                    isError = true,
                    supportingText = "Text exceeds the character limit"
                )
            ) }
            return
        }

        // If all the checks are passed, simply update the text in the text field
        _screenState.update { it.copy(
            messageTypeTextFieldState = it.messageTypeTextFieldState.copy(
                text = text,
                isError = false,
                supportingText = null
            )
        ) }
    }

    //endregion

    //region Behavior: addresses component

    /**
     * Will be called when the user would want to add a new phone address
     * to the rule. Should open the "add new phone address" dialog.
     */
    private fun onOpenAddNewAddressDialogRequest() {
        _screenState.update { it.copy(
            addNewAddressDialogState = AddNewAddressDialogState.Displayed(
                textFieldState = emptyTextFieldState(),
                canAddCurrentInputAsAddress = false
            )
        ) }
    }

    /**
     * Will be called when the user wants to remove a certain
     * phone address from the rule.
     */
    private fun onRemoveAddressRequest(address: String) {
        _screenState.update { it.copy(
            addressesBlockState = it.addressesBlockState.copy(
                addresses = buildList {
                    addAll(it.addressesBlockState.addresses)
                    remove(address)
                }
            )
        ) }
    }

    //endregion

    //region Behavior: filters component

    private fun onAddNewFilterRequest() {
//        val filterEditorDialogState = FilterEditorDialogState.Displayed(
//            textFieldState = emptyTextFieldState(),
//            filterTypeSelection = PresentationModel.ForwardingFilter.FilterType.INCLUDE,
//            ignoresCaseToggleState = false,
//            canAddCurrentInputAsFilter = false
//        ).let {
//            it.copy(
//                textFieldState = it.textFieldState.copy(
//                    supportingText = filterEditorDialogSupportingText(dialogState = it)
//                )
//            )
//        }

        _screenState.update { it.copy(
            filterEditorDialogState = FilterEditorDialogState.Displayed(
                textFieldState = emptyTextFieldState(),
                filterTypeSelection = PresentationModel.ForwardingFilter.FilterType.INCLUDE,
                ignoresCaseToggleState = false,
                canAddCurrentInputAsFilter = false
            )
        ) }
    }

    private fun onRemoveFilterRequest(filterID: String) {
        _screenState.update { state -> state.copy(
            filtersBlockState = state.filtersBlockState.copy(
                filters = buildList {
                    addAll(state.filtersBlockState.filters)
                    filter { it.id != filterID }
                }
            )
        ) }
    }

    //endregion

    //region Behavior: "add new phone address" dialog

    /**
     * Called whenever the input text in the phone address input text field
     * inside the "add new phone address dialog" wants to change
     */
    private fun onPhoneAddressInputChangeRequest(proposedNewText: String) {

        val canAddThisAddressToRule = canAddPhoneAddressToRule(proposedNewText)
        val inputDuplicatesAlreadyAddedAddress = isPhoneAddressDuplicatesAlreadyAddedAddress(proposedNewText)
        val inputIsBlank = proposedNewText.isBlank()

        val supportingText: String? = if (inputIsBlank) {
            "Number cannot be blank"
        } else if (inputDuplicatesAlreadyAddedAddress) {
            "This number is already added to the rule"
        } else {
            null
        }

        val isError = inputIsBlank || inputDuplicatesAlreadyAddedAddress

        _screenState.update { it.copy(
            addNewAddressDialogState = AddNewAddressDialogState.Displayed(
                textFieldState = TextFieldState(
                    text = proposedNewText,
                    isError = isError,
                    supportingText = supportingText,
                ),
                canAddCurrentInputAsAddress = canAddThisAddressToRule
            )
        ) }
    }

    /**
     * Called when the user wants to add a new address from the
     * "add new phone address" dialog
     */
    private fun addPhoneAddressFromNewPhoneAddressDialogInputs() {

        val dialogState = _screenState.value.addNewAddressDialogState
        if (dialogState !is AddNewAddressDialogState.Displayed) { return }
        val phoneAddress = dialogState.textFieldState.text
        if (!canAddPhoneAddressToRule(phoneAddress)) { return }

        _screenState.update { it.copy(
            addressesBlockState = it.addressesBlockState.copy(
                addresses = buildList {
                    addAll(it.addressesBlockState.addresses)
                    add(phoneAddress)
                }
            )
        ) }
    }

    /**
     * Called whenever the "add new address" dialog is dismissed.
     * This should happen after the fact of dismissal, and here
     * the dialog state would update accordingly to sync the state.
     */
    private fun onAddNewAddressDialogDismissal() {
        _screenState.update { it.copy(
            addNewAddressDialogState = AddNewAddressDialogState.NotDisplayed
        ) }
    }

    //endregion

    //region Behavior: filter editor dialog

    private fun onFilterEditorDialogFilterTypeSelectionChangeRequest(
        newSelection: PresentationModel.ForwardingFilter.FilterType
    ) {
        val dialogState =
            (_screenState.value.filterEditorDialogState as? FilterEditorDialogState.Displayed) ?: return
        val supportingText = filterEditorDialogSupportingText(dialogState, filterType = newSelection)

        _screenState.update { it.copy(
            filterEditorDialogState = dialogState.copy(
                filterTypeSelection = newSelection,
                textFieldState = dialogState.textFieldState.copy(
                    supportingText = supportingText
                )
            )
        ) }
    }

    private fun onFilterEditorCaseToggleSwitchRequest(
        newSelection: Boolean
    ) {
        val dialogState =
            (_screenState.value.filterEditorDialogState as? FilterEditorDialogState.Displayed) ?: return

        _screenState.update { it.copy(
            filterEditorDialogState = dialogState.copy(
                ignoresCaseToggleState = newSelection
            )
        ) }
    }

    /**
     * Called whenever the input text in the filtered text input text field
     * inside the filter editor dialog wants to change
     */
    private fun onFilterEditorDialogTextChangeRequest(proposedNewText: String) {

        val dialogState =
            (_screenState.value.filterEditorDialogState as? FilterEditorDialogState.Displayed) ?: return

        val isError = isFilterEditorDialogTextFieldInErrorState(dialogState, proposedNewText)
        val supportingText = filterEditorDialogSupportingText(dialogState, proposedNewText = proposedNewText)

        _screenState.update { it.copy(
            filterEditorDialogState = dialogState.copy(
                textFieldState = TextFieldState(
                    text = proposedNewText,
                    isError = isError,
                    supportingText = supportingText
                )
            )
        ) }
    }

    /**
     * Called when the user wants to add a new filter from the filter editor dialog.
     * Should form a forwarding filter from the current dialog inputs and add it to
     * the rule.
     */
    private fun addFilterFromFilterEditorDialogInputs() {

        val dialogState =
            (_screenState.value.filterEditorDialogState as? FilterEditorDialogState.Displayed) ?: return

        val filter = PresentationModel.ForwardingFilter(
            id = UUID.randomUUID().toString(),
            filterType = dialogState.filterTypeSelection,
            ignoreCase = dialogState.ignoresCaseToggleState,
            text = dialogState.textFieldState.text
        )

        _screenState.update { state -> state.copy(
            filtersBlockState = state.filtersBlockState.copy(
                filters = buildList {
                    addAll(state.filtersBlockState.filters)
                    add(filter)
                }
            )
        ) }
    }

    /**
     * Called whenever the filter editor dialog is dismissed.
     * This should happen after the fact of dismissal, and here
     * the dialog state would update accordingly to sync the state.
     */
    private fun onFilterEditorDialogDismissal() {
        _screenState.update { it.copy(
            filterEditorDialogState = FilterEditorDialogState.NotDisplayed
        ) }
    }

    //endregion

    //region Misc

    /**
     * Returns a [TextFieldState] with default parameters for an empty text field
     */
    private fun emptyTextFieldState(): TextFieldState {
        return TextFieldState(
            text = "",
            isError = false,
            supportingText = null
        )
    }

    /**
     * Checks if the specified phone address can be added to a rule.
     * As of now, simply checks that it's not blank and not a dupe.
     */
    private fun canAddPhoneAddressToRule(phoneAddress: String): Boolean {
        return phoneAddress.isNotBlank() && !isPhoneAddressDuplicatesAlreadyAddedAddress(phoneAddress)
    }

    /**
     * Checks if the specified phone address is a dupe of some other address added to this rule
     */
    private fun isPhoneAddressDuplicatesAlreadyAddedAddress(phoneAddress: String): Boolean {
        val currentlyAddedAddresses = _screenState.value.addressesBlockState.addresses
        return currentlyAddedAddresses.contains(phoneAddress)
    }

    /**
     * Returns a supporting text that matches the state of the filter editor (displayed) dialog
     *
     * @param dialogState Current state of the displayed dialog
     * @param filterType (Optional) Updated filter type. If present, the function will use this
     * in determining the according supporting text. If not present, it will use the filter type
     * from the current state.
     * @param proposedNewText (Optional) Proposed new filter text. If present, the function will
     * use this in determining the according supporting text. If not present, it will use the
     * filter text from the current state.
     */
    private fun filterEditorDialogSupportingText(
        dialogState: FilterEditorDialogState.Displayed,
        filterType: PresentationModel.ForwardingFilter.FilterType? = null,
        proposedNewText: String? = null
    ): String {

        val selectedFilterType = filterType ?: dialogState.filterTypeSelection
        val isError = isFilterEditorDialogTextFieldInErrorState(dialogState, proposedNewText)
        val caseText = when (dialogState.ignoresCaseToggleState) {
            true -> "(ignoring case)"
            false -> "(respecting case)"
        }
        val supportingText = if (isError) {
            "Filter text cannot be blank"
        } else {
            when (selectedFilterType) {
                PresentationModel.ForwardingFilter.FilterType.INCLUDE -> {
                    "A message would pass this filter when the specified text $caseText is present in it"
                }
                PresentationModel.ForwardingFilter.FilterType.EXCLUDE -> {
                    "A message would pass this filter when the specified text $caseText is NOT present in it"
                }
            }
        }
        return supportingText
    }

    /**
     * Returns filter editor (displayed) dialog text field error state
     *
     * @param dialogState Current state of the displayed dialog
     * @param proposedNewText (Optional) Proposed new filter text. If present, the function will
     * use this in determining the according error state. If not present, it will use the
     * filter text from the current state.
     */
    private fun isFilterEditorDialogTextFieldInErrorState(
        dialogState: FilterEditorDialogState.Displayed,
        proposedNewText: String? = null
    ): Boolean {
        val filterText = proposedNewText ?: dialogState.textFieldState.text
        return filterText.isEmpty()
    }

    //endregion
}
