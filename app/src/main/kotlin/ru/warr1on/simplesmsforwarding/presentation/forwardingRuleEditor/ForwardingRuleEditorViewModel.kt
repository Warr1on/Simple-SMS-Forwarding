package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository

class ForwardingRuleEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val rulesRepo: ForwardingRulesRepository
) : ViewModel() {

    private var ruleID: String? = null

    var screenTitle by mutableStateOf("New rule")
        private set

    val ruleName = MutableStateFlow("")

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

        ruleID?.let { setupEditorForEditingRule(it) }
    }

    private fun setupEditorForEditingRule(ruleID: String) {
        screenTitle = "Rule editor"
        viewModelScope.launch {
            val rule = rulesRepo.getRule(ruleID)
        }
    }
}
