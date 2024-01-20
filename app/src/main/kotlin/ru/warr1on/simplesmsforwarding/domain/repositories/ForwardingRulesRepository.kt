package ru.warr1on.simplesmsforwarding.domain.repositories

import ru.warr1on.simplesmsforwarding.domain.mapping.mapToForwardingRule
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToPersistedCompleteRule
import ru.warr1on.simplesmsforwarding.domain.mapping.mapToPersistedFilter
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingFilter
import ru.warr1on.simplesmsforwarding.data.local.dao.ForwardingRulesDao
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedRuleAssociatedPhoneAddress

interface ForwardingRulesRepository {

    suspend fun getAllRules(): List<ForwardingRule>

    suspend fun addRule(rule: ForwardingRule)

    suspend fun deleteRule(ruleID: String)

    suspend fun applyPhoneAddressToRule(address: String, ruleID: String)

    suspend fun removeAddressFromApplicableToRule(address: String, ruleID: String)

    suspend fun addFilterToRule(filter: ForwardingFilter, ruleID: String)

    suspend fun removeFilterFromRule(filter: ForwardingFilter, ruleID: String)

    object Factory {

        fun getDefaultRepo(rulesDao: ForwardingRulesDao): ForwardingRulesRepository {
            return ForwardingRulesRepositoryImpl(rulesDao)
        }
    }
}

private class ForwardingRulesRepositoryImpl(
    val rulesDao: ForwardingRulesDao
) : ForwardingRulesRepository {

    override suspend fun getAllRules(): List<ForwardingRule> {
        return rulesDao.getAllRules().map { it.mapToForwardingRule() }
    }

    override suspend fun addRule(rule: ForwardingRule) {
        val persistenceModel = rule.mapToPersistedCompleteRule()
        rulesDao.insertRule(persistenceModel)
//        withContext(Dispatchers.IO) {
//
//        }
    }

    override suspend fun deleteRule(ruleID: String) {
        rulesDao.deleteRule(ruleID)
    }

    override suspend fun applyPhoneAddressToRule(address: String, ruleID: String) {
        val persistenceModel = PersistedRuleAssociatedPhoneAddress(ruleID, address)
        rulesDao.insertRuleAssociatedPhoneAddresses(persistenceModel)
    }

    override suspend fun removeAddressFromApplicableToRule(address: String, ruleID: String) {
        val persistenceModel = PersistedRuleAssociatedPhoneAddress(ruleID, address)
        rulesDao.deleteRuleAssociatedPhoneAddress(persistenceModel)
    }

    override suspend fun addFilterToRule(filter: ForwardingFilter, ruleID: String) {
        val persistenceModel = filter.mapToPersistedFilter(ruleID)
        rulesDao.insertForwardingFilter(persistenceModel)
    }

    override suspend fun removeFilterFromRule(filter: ForwardingFilter, ruleID: String) {
        val persistenceModel = filter.mapToPersistedFilter(ruleID)
        rulesDao.deleteForwardingFilter(persistenceModel)
    }
}
