package ru.warr1on.simplesmsforwarding.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.warr1on.simplesmsforwarding.data.local.LocalDataStore.*

interface LocalDataStore {

    val storedData: StateFlow<StoredData>

    suspend fun getStringValue(key: String): String?

    suspend fun getIntValue(key: String): Int?

    suspend fun writeValue(stringValue: String, key: String)

    suspend fun writeValue(intValue: Int, key: String)

    data class StoredData(
        val stringValues: Map<String, String>,
        val intValues: Map<String, Int>
    )

    object Factory {

        fun getDefaultDataStore(context: Context, coroutineScope: CoroutineScope): LocalDataStore {
            return LocalDataStoreImpl(context, coroutineScope)
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saved_values")

private class LocalDataStoreImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope

) : LocalDataStore {

    private val _storedData = MutableStateFlow(StoredData(emptyMap(), emptyMap()))
    override val storedData: StateFlow<StoredData> = _storedData

    init {
        coroutineScope.launch {
            context.dataStore.data.collect {
                _storedData.value = it.mapToStoredData()
            }
        }
    }

    override suspend fun getStringValue(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[prefKey]
    }

    override suspend fun getIntValue(key: String): Int? {
        val prefKey = intPreferencesKey(key)
        return context.dataStore.data.first()[prefKey]
    }

    override suspend fun writeValue(stringValue: String, key: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[prefKey] = stringValue
        }
    }

    override suspend fun writeValue(intValue: Int, key: String) {
        val prefKey = intPreferencesKey(key)
        context.dataStore.edit {
            it[prefKey] = intValue
        }
    }
}

private fun Preferences.mapToStoredData(): StoredData {
    val prefsMap = this.asMap()
    val stringValues = mutableMapOf<String, String>()
    val intValues = mutableMapOf<String, Int>()
    prefsMap.entries.forEach {
        when (val value = it.value) {
            is String -> stringValues[it.key.name] = value
            is Int -> intValues[it.key.name] = value
        }
    }
    return StoredData(stringValues, intValues)
}
