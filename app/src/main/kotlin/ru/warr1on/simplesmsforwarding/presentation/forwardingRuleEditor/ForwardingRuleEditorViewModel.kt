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

    private var ruleID: String? = savedStateHandle["ruleId"]

    var screenTitle by mutableStateOf("New rule")
        private set

    val ruleName = MutableStateFlow("")

    init {
        ruleID?.let { setupEditorForEditingRule(it) }
    }

    private fun setupEditorForEditingRule(ruleID: String) {
        screenTitle = "Rule editor"
        viewModelScope.launch {
            rulesRepo.getRule(ruleID)
        }
    }
}
