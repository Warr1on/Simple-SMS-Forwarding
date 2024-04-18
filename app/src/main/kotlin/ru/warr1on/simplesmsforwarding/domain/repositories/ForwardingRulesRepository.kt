package ru.warr1on.simplesmsforwarding.domain.repositories

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToForwardingRule
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToPersistedCompleteRule
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToPersistedFilter
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingFilter
import ru.warr1on.simplesmsforwarding.data.local.dao.ForwardingRulesDao
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedRuleAssociatedPhoneAddress

interface ForwardingRulesRepository {

    val forwardingRules: StateFlow<List<ForwardingRule>>

    suspend fun getAllRules(): List<ForwardingRule>

    suspend fun getRule(ruleID: String): ForwardingRule?

    suspend fun addRule(rule: ForwardingRule)

    suspend fun deleteRule(ruleID: String)

    suspend fun applyPhoneAddressToRule(address: String, ruleID: String)

    suspend fun removeAddressFromApplicableToRule(address: String, ruleID: String)

    suspend fun addFilterToRule(filter: ForwardingFilter, ruleID: String)

    suspend fun removeFilterFromRule(filter: ForwardingFilter, ruleID: String)

    object Factory {

        fun getDefaultRepo(
            rulesDao: ForwardingRulesDao,
            coroutineScope: CoroutineScope
        ): ForwardingRulesRepository {
            return ForwardingRulesRepositoryImpl(rulesDao, coroutineScope)
        }
    }
}

private class ForwardingRulesRepositoryImpl(
    private val rulesDao: ForwardingRulesDao,
    private val coroutineScope: CoroutineScope
) : ForwardingRulesRepository {

    private val _forwardingRules = MutableStateFlow<List<ForwardingRule>>(emptyList())
    override val forwardingRules: StateFlow<List<ForwardingRule>> get() {
        if (shouldLoadRules) {
            shouldLoadRules = false
            coroutineScope.launch { updateRulesList() }
        }
        return _forwardingRules
    }

    private var shouldLoadRules = true

    override suspend fun getAllRules(): List<ForwardingRule> {
        val rules = rulesDao.getAllRules().map { it.mapToForwardingRule() }
        _forwardingRules.value = rules
        return rules
    }

    override suspend fun getRule(ruleID: String): ForwardingRule? {
        return rulesDao.getRule(ruleID)?.mapToForwardingRule()
    }

    override suspend fun addRule(rule: ForwardingRule) {
        coroutineScope.launch {
            val persistenceModel = rule.mapToPersistedCompleteRule()
            rulesDao.insertRule(persistenceModel)
            updateRulesList()
        }.join()
    }

    override suspend fun deleteRule(ruleID: String) {
        coroutineScope.launch {
            rulesDao.deleteRule(ruleID)
            updateRulesList()
        }.join()
    }

    override suspend fun applyPhoneAddressToRule(address: String, ruleID: String) {
        coroutineScope.launch {
            val persistenceModel = PersistedRuleAssociatedPhoneAddress(ruleID, address)
            rulesDao.insertRuleAssociatedPhoneAddresses(persistenceModel)
            updateRulesList()
        }.join()
    }

    override suspend fun removeAddressFromApplicableToRule(address: String, ruleID: String) {
        coroutineScope.launch {
            val persistenceModel = PersistedRuleAssociatedPhoneAddress(ruleID, address)
            rulesDao.deleteRuleAssociatedPhoneAddress(persistenceModel)
            updateRulesList()
        }.join()
    }

    override suspend fun addFilterToRule(filter: ForwardingFilter, ruleID: String) {
        coroutineScope.launch {
            val persistenceModel = filter.mapToPersistedFilter(ruleID)
            rulesDao.insertForwardingFilter(persistenceModel)
            updateRulesList()
        }.join()
    }

    override suspend fun removeFilterFromRule(filter: ForwardingFilter, ruleID: String) {
        coroutineScope.launch {
            val persistenceModel = filter.mapToPersistedFilter(ruleID)
            rulesDao.deleteForwardingFilter(persistenceModel)
            updateRulesList()
        }.join()
    }

    private suspend fun updateRulesList() {
        val rules = rulesDao.getAllRules().map { it.mapToForwardingRule() }
        _forwardingRules.value = rules
    }
}
