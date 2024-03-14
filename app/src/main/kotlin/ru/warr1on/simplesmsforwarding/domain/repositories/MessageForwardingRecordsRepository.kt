package ru.warr1on.simplesmsforwarding.domain.repositories

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.warr1on.simplesmsforwarding.data.local.dao.MessageForwardingRecordsDao
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToMessageForwardingRecord
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToPersistedRecord
import ru.warr1on.simplesmsforwarding.domain.model.MessageForwardingRecord

// TODO: Adding paging should be considered

interface MessageForwardingRecordsRepository {

    val records: StateFlow<List<MessageForwardingRecord>>

    suspend fun getAllStoredRecords(): List<MessageForwardingRecord>

    suspend fun addRecord(record: MessageForwardingRecord)

    suspend fun updateRecord(record: MessageForwardingRecord)

    suspend fun deleteRecord(recordID: String)

    object Factory {

        fun getDefaultRepo(
            recordsDao: MessageForwardingRecordsDao,
            ioDispatcher: CoroutineDispatcher,
            parentScope: CoroutineScope
        ): MessageForwardingRecordsRepository {
            return MessageForwardingRecordsRepositoryImpl(
                recordsDao,
                ioDispatcher,
                parentScope
            )
        }
    }
}

private class MessageForwardingRecordsRepositoryImpl(
    private val recordsDao: MessageForwardingRecordsDao,
    private val ioDispatcher: CoroutineDispatcher,
    parentScope: CoroutineScope
) : MessageForwardingRecordsRepository {

    private val _records = MutableStateFlow<List<MessageForwardingRecord>>(emptyList())
    override val records: StateFlow<List<MessageForwardingRecord>> = _records

    private val coroutineScope = parentScope + SupervisorJob(parent = parentScope.coroutineContext.job)

    init {
        updateRecordsList()
    }

    override suspend fun getAllStoredRecords(): List<MessageForwardingRecord> {
        val storedRecords = recordsDao.getAll()
            .map { it.mapToMessageForwardingRecord() }
            .reversed()
        _records.value = storedRecords
        return storedRecords
    }

    override suspend fun addRecord(record: MessageForwardingRecord) {
        recordsDao.insertRecord(record.mapToPersistedRecord())
        updateRecordsList()
    }

    override suspend fun updateRecord(record: MessageForwardingRecord) {
        recordsDao.updateRecord(record.mapToPersistedRecord())
        updateRecordsList()
    }

    override suspend fun deleteRecord(recordID: String) {
        recordsDao.deleteRecord(recordID)
        updateRecordsList()
    }

    private fun updateRecordsList() {
        coroutineScope.launch {
            _records.value = recordsDao.getAll()
                .map { it.mapToMessageForwardingRecord() }
                .reversed()
        }
    }
}
