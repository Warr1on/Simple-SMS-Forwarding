package ru.warr1on.simplesmsforwarding.domain.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.data.local.LocalDataStore

/**
 * Locally stored forwarder app settings
 *
 * @param    botURL The URL endpoint that is used to communicate with the forwarder bot backend
 * @param senderKey The personal key of the sender, used by the backend to determine from
 *                  whom the message was forwarded and to whom it should be forwarded to.
 */
data class ForwarderSettings(
    val botURL: String?,
    val senderKey: String?
)

sealed class ForwarderSetting(val key: String) {

    sealed class StringSetting(val value: String, key: String) : ForwarderSetting(key) {

        /**
         * The URL endpoint that is used to communicate with the forwarder bot backend
         */
        class BotURL(url: String) : StringSetting(url, "forwarder_bot_url")

        /**
         * The personal key of the sender, used by the backend to determine from
         * whom the message was forwarded and to whom it should be forwarded to
         */
        class SenderKey(senderKey: String) : StringSetting(senderKey, "sender_key")
    }

    sealed class IntSetting(val value: Int, key: String) : ForwarderSetting(key)
}

interface ForwarderSettingsRepository {

    val currentSettings: StateFlow<ForwarderSettings?>

    suspend fun updateSetting(setting: ForwarderSetting)

    object Factory {

        fun getDefaultRepo(
            dataStore: LocalDataStore,
            coroutineScope: CoroutineScope
        ): ForwarderSettingsRepository {
            return ForwarderSettingsRepositoryImpl(dataStore, coroutineScope)
        }
    }
}

private class ForwarderSettingsRepositoryImpl(
    private val dataStore: LocalDataStore,
    private val coroutineScope: CoroutineScope
) : ForwarderSettingsRepository {

    private val _currentSettings = MutableStateFlow<ForwarderSettings?>(null)
    override val currentSettings = _currentSettings.asStateFlow()

    init {
        coroutineScope.launch {
            dataStore.storedData.collect {
                _currentSettings.value = settingsFromStoredData(it)
            }
        }
    }

    override suspend fun updateSetting(setting: ForwarderSetting) {
        when (setting) {
            is ForwarderSetting.StringSetting -> {
                dataStore.writeValue(setting.value, key = setting.key)
            }
            is ForwarderSetting.IntSetting -> {
                dataStore.writeValue(setting.value, key = setting.key)
            }
        }
    }

    private fun settingsFromStoredData(storedData: LocalDataStore.StoredData): ForwarderSettings {
        val botURL = storedData.stringValues["forwarder_bot_url"]
        val senderKey = storedData.stringValues["sender_key"]
        return ForwarderSettings(
            botURL = botURL,
            senderKey = senderKey
        )
    }
}
