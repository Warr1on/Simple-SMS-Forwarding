package ru.warr1on.simplesmsforwarding.data.local.model

import androidx.room.*

/**
 * A locally persisted forwarding rule's description.
 * This description contains only the rule's ID, name and type key.
 *
 * Forwarding rules are a combination of filters that are used to determine if
 * the specific message should be forwarded, and addresses that a rule is applied to.
 *
 * @param      id Rule's unique identifier
 * @param    name Rule's name. Used just for user's convenience
 * @param typeKey A unique key that is used to identify the type of a forwarded message
 *                on the backend service, so that it would know what kind of message it
 *                received and to whom it should forward the message to.
 */
@Entity(tableName = PersistedForwardingRuleDescription.tableName)
data class PersistedForwardingRuleDescription(

    @PrimaryKey
    @ColumnInfo(name = Keys.id)
    val id: String,

    @ColumnInfo(name = Keys.name)
    val name: String,

    @ColumnInfo(name = Keys.typeKey)
    val typeKey: String
) {
    companion object {
        const val tableName = "forwarding_rule"
    }
    object Keys {
        const val id = "id"
        const val name = "name"
        const val typeKey = "type_key"
    }
}

/**
 * A full locally persisted forwarding rule.
 *
 * Contains a [PersistedForwardingRuleDescription], and lists of applicable addresses
 * ([PersistedRuleAssociatedPhoneAddress]) and filters ([PersistedForwardingFilter])
 *
 * @param     ruleDescription This contains the rule's id, name and type key
 * @param applicableAddresses List of phone addresses that this rule applies to
 * @param             filters List of filters that are used by the rule to determine
 *                            if a specific message passes the defined criteria
 */
data class PersistedForwardingRule(

    @Embedded
    val ruleDescription: PersistedForwardingRuleDescription,

    @Relation(
        parentColumn = PersistedForwardingRuleDescription.Keys.id,
        entityColumn = PersistedRuleAssociatedPhoneAddress.Keys.ruleID
    )
    val applicableAddresses: List<PersistedRuleAssociatedPhoneAddress>,

    @Relation(
        parentColumn = "id",
        entityColumn = PersistedForwardingFilter.Keys.ruleID
    )
    val filters: List<PersistedForwardingFilter>
)
