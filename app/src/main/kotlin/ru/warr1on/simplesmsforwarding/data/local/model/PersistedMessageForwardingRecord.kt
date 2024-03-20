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

    @ColumnInfo(name = Keys.forwardingStatus)
    val forwardingStatus: Status,

    @ColumnInfo(name = Keys.resultDescription)
    val resultDescription: String
) {
    enum class Status {
        PENDING, SUCCESS, PARTIAL_SUCCESS, FAILURE
    }

    companion object {
        const val tableName = "message_forwarding_record"
    }

    object Keys {
        const val id = "id"
        const val messageAddress = "msg_address"
        const val messageBody = "msg_body"
        const val forwardingStatus = "forwarding_status"
        const val resultDescription = "result_description"
    }
}
