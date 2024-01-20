package ru.warr1on.simplesmsforwarding.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A locally persisted forwarding filter.
 *
 * Filters are used in forwarding rules to determine
 * if a specific message should be forwarded.
 *
 * @param          id A filter's unique identifier
 * @param      ruleID The ID of the rule that is associated with this filter
 * @param  filterType Filter type (inclusion/exclusion of the specified text)
 * @param  filterText The specific text that is used to filter an incoming message
 * @param ignoresCase Determines if the case should be respected or ignored
 *                    when checking if the message text passes the filter
 */
@Entity(tableName = PersistedForwardingFilter.tableName)
data class PersistedForwardingFilter(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = Keys.ruleID)
    val ruleID: String,

    @ColumnInfo(name = Keys.filterType)
    val filterType: String,

    @ColumnInfo(name = Keys.filterText)
    val filterText: String,

    @ColumnInfo(name = Keys.ignoresCase)
    val ignoresCase: Boolean
) {
    companion object {
        const val tableName = "forwarding_filter"
    }

    object Keys {
        const val ruleID = "rule_id"
        const val filterType = "filter_type"
        const val filterText = "filter_text"
        const val ignoresCase = "ignores_case"
    }
    object Values {
        const val filterTypeInclude = "INCLUDE"
        const val filterTypeExclude = "EXCLUDE"
    }
}
