package ru.warr1on.simplesmsforwarding.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * A locally persisted phone address
 */
@Entity(tableName = PersistedPhoneAddress.tableName)
data class PersistedPhoneAddress(

    @PrimaryKey
    @ColumnInfo(name = Keys.address)
    val address: String
) {
    companion object {
        const val tableName = "phone_address"
    }

    object Keys {
        const val address = "address"
    }
}

/**
 * A locally persisted phone address that is attached to a forwarding rule.
 *
 * The combination of rule ID and a phone address should be unique, so both
 * of these fields are used as a composite primary key.
 *
 * @param  ruleID An ID of the forwarding rule this address is attached to
 * @param address The value of a phone address
 */
@Entity(
    tableName = PersistedRuleAssociatedPhoneAddress.tableName,
    primaryKeys = [
        PersistedRuleAssociatedPhoneAddress.Keys.ruleID,
        PersistedRuleAssociatedPhoneAddress.Keys.address
    ],
    foreignKeys = [
        ForeignKey(
            entity = PersistedForwardingRuleDescription::class,
            parentColumns = [PersistedForwardingRuleDescription.Keys.id],
            childColumns = [PersistedRuleAssociatedPhoneAddress.Keys.ruleID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PersistedRuleAssociatedPhoneAddress(

    @ColumnInfo(name = Keys.ruleID)
    val ruleID: String,

    @ColumnInfo(name = Keys.address)
    val address: String
) {
    companion object {
        const val tableName = "rule_associated_address"
    }

    object Keys {
        const val id = "id"
        const val ruleID = "rule_id"
        const val address = "address"
    }
}
