package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSetting
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSettingsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel

class ForwardingSettingsViewModel(
    private val rulesRepo: ForwardingRulesRepository,
    private val settingsRepo: ForwarderSettingsRepository
) : ViewModel() {

    private val _botUrlStateFlow = MutableStateFlow("")
    val botUrlStateFlow = _botUrlStateFlow.asStateFlow()

    private val _senderKeyStateFlow = MutableStateFlow("")
    val senderKeyStateFlow = _senderKeyStateFlow.asStateFlow()

    private val _rulesStateFlow = MutableStateFlow<List<PresentationModel.ForwardingRule>>(emptyList())
    val rulesStateFlow = _rulesStateFlow.asStateFlow()

    init {
        subscribeToRepoFlowUpdates()
    }

    private fun subscribeToRepoFlowUpdates() {
        viewModelScope.launch {
            settingsRepo.currentSettings.collect {
                _botUrlStateFlow.value = it?.botURL ?: ""
                _senderKeyStateFlow.value = it?.senderKey ?: ""
            }
        }
    }

    fun updateBotUrl(newBotUrl: String) {
        viewModelScope.launch {
            settingsRepo.updateSetting(ForwarderSetting.StringSetting.BotURL(newBotUrl))
        }
    }

    fun updateSenderKey(newSenderKey: String) {
        viewModelScope.launch {
            settingsRepo.updateSetting(ForwarderSetting.StringSetting.SenderKey(newSenderKey))
        }
    }
}
