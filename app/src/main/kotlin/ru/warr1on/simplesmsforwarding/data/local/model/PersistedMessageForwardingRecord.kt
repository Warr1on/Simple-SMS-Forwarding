package ru.warr1on.simplesmsforwarding.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a persistent record of a message that
 * was qualified as such that should be forwarded
 */
@Entity(tableName = PersistedMessageForwardingRecord.tableName)
data class PersistedMessageForwardingRecord(

    @PrimaryKey
    @ColumnInfo(name = Keys.id)
    val id: String,

    @ColumnInfo(name = Keys.messageAddress)
    val messageAddress: String,

    @ColumnInfo(name = Keys.messageBody)
    val messageBody: String,

    @ColumnInfo(name = Keys.isFulfilled)
    val isFulfilled: Boolean,

    @ColumnInfo(name = Keys.resultDescription)
    val resultDescription: String
) {
    companion object {
        const val tableName = "message_forwarding_record"
    }

    object Keys {
        const val id = "id"
        const val messageAddress = "msg_address"
        const val messageBody = "msg_body"
        const val isFulfilled = "is_fulfilled"
        const val resultDescription = "result_description"
    }
}
