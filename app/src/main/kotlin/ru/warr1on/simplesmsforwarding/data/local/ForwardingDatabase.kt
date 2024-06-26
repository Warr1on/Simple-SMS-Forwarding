package ru.warr1on.simplesmsforwarding.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.warr1on.simplesmsforwarding.data.local.dao.ForwardingRulesDao
import ru.warr1on.simplesmsforwarding.data.local.dao.MessageForwardingRecordsDao
import ru.warr1on.simplesmsforwarding.data.local.model.*

/**
 * A database that is used by the forwarder
 */
@Database(
    version = 1,
    entities = [
        PersistedMessageForwardingRecord::class,
        PersistedForwardingRuleDescription::class,
        PersistedRuleAssociatedPhoneAddress::class,
        PersistedForwardingFilter::class,
        PersistedPhoneAddress::class
    ]
)
abstract class ForwardingDatabase : RoomDatabase() {

    abstract fun forwardingRequestDao(): MessageForwardingRecordsDao

    abstract fun forwardingRulesDao(): ForwardingRulesDao

    companion object {
        const val databaseName = "sms-forwarding-db"
    }
}
