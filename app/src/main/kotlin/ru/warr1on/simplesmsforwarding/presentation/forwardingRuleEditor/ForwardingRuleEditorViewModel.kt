package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model.ForwardingRuleEditorScreenState
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model.ForwardingRuleEditorScreenState.*
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model.ForwardingRuleEditorScreenState.AddressesBlockState.*

/**
 * A view model for the forwarding rule editor screen
 */
class ForwardingRuleEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val rulesRepo: ForwardingRulesRepository
) : ViewModel() {

    private val _screenState = MutableStateFlow(generateInitialScreenState())
    /** The current state of the forwarding rule editor screen */
    val screenState = _screenState.asStateFlow()


    //--- Setup ---//

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

    private fun generateInitialScreenState(): ForwardingRuleEditorScreenState {

        val initialTextFieldState: (onTextChange: (String) -> Unit) -> TextFieldState = {
            TextFieldState(
                text = "",
                isError = false,
                supportingText = "",
                onTextChange = it
            )
        }

        val initialAddressesBlockState: () -> AddressesBlockState = {
            AddressesBlockState(
                addresses = emptyList(),
                modalState = ModalState.NONE,
                onAddAddressRequest = ::onAddAddressRequest,
                onRemoveAddressRequest = ::onRemoveAddressRequest
            )
        }

        return ForwardingRuleEditorScreenState(
            screenTitle = "New rule",
            ruleNameTextFieldState = initialTextFieldState(::onRuleNameTextChange),
            messageTypeTextFieldState = initialTextFieldState(::onMessageTypeTextChange),
            addressesBlockState = initialAddressesBlockState()
        )
    }


    //--- Behavior: rule name and message type key text fields component ---//

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


    //--- Behavior: addresses component ---//

    private fun onAddAddressRequest(address: String) {
        _screenState.update { it.copy(
            addressesBlockState = it.addressesBlockState.copy(
                addresses = buildList {
                    addAll(it.addressesBlockState.addresses)
                    add(address)
                },
                modalState = ModalState.NONE
            )
        ) }
    }

    private fun onRemoveAddressRequest(address: String) {
        _screenState.update { it.copy(
            addressesBlockState = it.addressesBlockState.copy(
                addresses = buildList {
                    addAll(it.addressesBlockState.addresses)
                    remove(address)
                },
                modalState = ModalState.NONE
            )
        ) }
    }

    /**
     * A validator that checks if the specified phone address can be added to a rule.
     */
    private fun validatePhoneAddress(address: String): Boolean {
        return true
    }
}
