package ru.warr1on.simplesmsforwarding.data.local.dao

import androidx.room.*
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedForwardingFilter
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedForwardingRule
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedForwardingRuleDescription
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedRuleAssociatedPhoneAddress

private typealias RuleDescription = PersistedForwardingRuleDescription
private typealias RuleDescriptionKeys = PersistedForwardingRuleDescription.Keys
private typealias PhoneAddress = PersistedRuleAssociatedPhoneAddress
private typealias PhoneAddressKeys = PersistedRuleAssociatedPhoneAddress.Keys
private typealias Filter = PersistedForwardingFilter
private typealias FilterKeys = PersistedForwardingFilter.Keys

/**
 * A DAO for automatic forwarding rules, including
 * the applicable addresses and filters they have
 */
@Dao
abstract class ForwardingRulesDao {

    // --- Forwarding rules --- //

    /**
     * Retrieves all forwarding rules with the addresses
     * they apply to and their filters
     */
    @Transaction
    @Query("SELECT * FROM ${RuleDescription.tableName}")
    abstract suspend fun getAllRules(): List<PersistedForwardingRule>

    @Transaction
    @Query("")
    suspend fun insertRule(rule: PersistedForwardingRule) {
        insertRuleDescription(rule.ruleDescription)
        insertRuleAssociatedPhoneAddresses(*rule.applicableAddresses.toTypedArray())
        insertForwardingFilters(*rule.filters.toTypedArray())
    }

    /**
     * Inserts a new forwarding rule description (i.e. without
     * applicable addresses and filters) in the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRuleDescription(ruleDescription: PersistedForwardingRuleDescription)

    /**
     * Updates a new forwarding rule description (i.e. without
     * applicable addresses and filters) in the database
     */
    @Update
    abstract suspend fun updateRuleDescription(ruleDescription: PersistedForwardingRuleDescription)

    /**
     * Deletes the forwarding rule from the database
     */
    @Delete
    abstract suspend fun deleteRule(rule: PersistedForwardingRuleDescription)

    /**
     * Deletes the forwarding rule with the specified ID from the database
     */
    @Query(
        "DELETE FROM ${RuleDescription.tableName} " +
        "WHERE ${RuleDescriptionKeys.id} = :id"
    )
    abstract suspend fun deleteRule(id: String)


    // --- Rule-associated phone addresses --- //

    @Query("SELECT * FROM ${PhoneAddress.tableName} WHERE ${PhoneAddressKeys.ruleID} = :ruleID")
    abstract suspend fun getRuleAssociatedPhoneAddresses(ruleID: String): List<PersistedRuleAssociatedPhoneAddress>

    @Insert
    abstract suspend fun insertRuleAssociatedPhoneAddresses(vararg addresses: PersistedRuleAssociatedPhoneAddress)

    @Update
    abstract suspend fun updateRuleAssociatedPhoneAddress(address: PersistedRuleAssociatedPhoneAddress)

    @Delete
    abstract suspend fun deleteRuleAssociatedPhoneAddress(address: PersistedRuleAssociatedPhoneAddress)


    // --- Forwarding filters --- //

    @Query("SELECT * FROM ${Filter.tableName} WHERE ${FilterKeys.ruleID} = :ruleID")
    abstract suspend fun getFiltersForRule(ruleID: String): List<PersistedForwardingFilter>

    @Insert
    abstract suspend fun insertForwardingFilter(filter: PersistedForwardingFilter)

    @Insert
    abstract suspend fun insertForwardingFilters(vararg filters: PersistedForwardingFilter)

    @Update
    abstract suspend fun updateForwardingFilter(filter: PersistedForwardingFilter)

    @Delete
    abstract suspend fun deleteForwardingFilter(filter: PersistedForwardingFilter)
}
