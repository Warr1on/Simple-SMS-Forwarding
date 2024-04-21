package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.presentation.core.components.ImmutableWrappedList
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel

class ForwardingRuleEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val rulesRepo: ForwardingRulesRepository
) : ViewModel() {

    /**
     * The title of the rule editor screen.
     *
     * Will either be "New rule" for the creation of a new rule,
     * or "Rule editor" for editing an existing one.
     */
    var screenTitle by mutableStateOf("New rule")
        private set

    val ruleName = MutableStateFlow("")

    val messageTypeKey = MutableStateFlow("")

    private val _phoneAddresses = MutableStateFlow(
        ImmutableWrappedList.unsafeInit(emptyList<String>())
    )
    val phoneAddresses = _phoneAddresses.asStateFlow()

    private val _textFilters = MutableStateFlow(
        ImmutableWrappedList.unsafeInit(emptyList<PresentationModel.ForwardingFilter>())
    )
    val textFilters = _textFilters.asStateFlow()

    /**
     * A validator that checks if the specified phone address can be added to a rule.
     *
     * As phone addresses can be vary from the normal phone numbers to short numbers
     * to text addresses, there are no strict format rules, so most it will do is it
     * will check if the address is not a duplicate of some other that's already been
     * added to a rule.
     *
     * This lambda will take the address to validate as the parameter, and will return
     * a boolean flag signalizing if the address can be added or not.
     */
    val phoneAddressValidator: (address: String) -> Boolean
        get() = { validatePhoneAddress(it) }

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
        // expect when having to deal with Android's garbage APIs ¯\_(ツ)_/¯

        ruleID?.let { setupEditorForEditingRuleWithID(it) }
    }

    private fun setupEditorForEditingRuleWithID(ruleID: String) {
        screenTitle = "Rule editor"
        viewModelScope.launch {
            val rule = rulesRepo.getRule(ruleID)
            if (rule != null) {
                setupEditorAccordingToRule(rule)
            } else {

            }
        }
    }

    private fun setupEditorAccordingToRule(rule: ForwardingRule) {
        ruleName.value = rule.name
        messageTypeKey.value = rule.typeKey
    }

    /**
     * A validator that checks if the specified phone address can be added to a rule.
     */
    private fun validatePhoneAddress(address: String): Boolean {
        return true
    }
}
