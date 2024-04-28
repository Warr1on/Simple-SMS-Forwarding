package ru.warr1on.simplesmsforwarding.data.local.dao

import androidx.room.*
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedMessageForwardingRecord

@Dao
interface MessageForwardingRecordsDao {

    @Query("SELECT * FROM ${PersistedMessageForwardingRecord.tableName}")
    suspend fun getAll(): List<PersistedMessageForwardingRecord>

    @Insert
    suspend fun insertRecord(record: PersistedMessageForwardingRecord)

    @Update
    suspend fun updateRecord(record: PersistedMessageForwardingRecord)

    @Delete
    suspend fun deleteRecord(record: PersistedMessageForwardingRecord)

    @Query(
        "DELETE FROM ${PersistedMessageForwardingRecord.tableName} " +
        "WHERE ${PersistedMessageForwardingRecord.Keys.id} = :recordID"
    )
    suspend fun deleteRecord(recordID: String)
}
