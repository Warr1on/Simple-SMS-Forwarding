package ru.warr1on.simplesmsforwarding.domain.mapping

import ru.warr1on.simplesmsforwarding.data.local.model.*
import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingFilter
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.model.MessageForwardingRecord

fun MessageForwardingRecord.mapToPersistedRecord(): PersistedMessageForwardingRecord {
    return PersistedMessageForwardingRecord(
        id = this.id,
        messageAddress = this.messageAddress,
        messageBody = this.messageBody,
        isFulfilled = this.isFulfilled,
        resultDescription = this.resultDescription
    )
}

fun ForwardingRule.mapToPersistedCompleteRule(): PersistedForwardingRule {

    val mapPhoneAddresses: (List<String>) -> List<PersistedRuleAssociatedPhoneAddress> = { addresses ->
        addresses.map {
            PersistedRuleAssociatedPhoneAddress(
                ruleID = this.id,
                address = it
            )
        }
    }

    return PersistedForwardingRule(
        ruleDescription = this.mapToPersistedRuleDescription(),
        applicableAddresses = mapPhoneAddresses(this.applicableAddresses),
        filters = this.filters.map { it.mapToPersistedFilter(this.id) }
    )
}

fun ForwardingRule.mapToPersistedRuleDescription(): PersistedForwardingRuleDescription {
    return PersistedForwardingRuleDescription(
        id = this.id,
        name = this.name,
        typeKey = this.typeKey
    )
}

fun ForwardingFilter.mapToPersistedFilter(ruleID: String): PersistedForwardingFilter {

    val filterType = when (this.filterType) {
        FilterType.INCLUDE -> PersistedForwardingFilter.Values.filterTypeInclude
        FilterType.EXCLUDE -> PersistedForwardingFilter.Values.filterTypeExclude
    }

    return PersistedForwardingFilter(
        id = this.id,
        ruleID = ruleID,
        filterType = filterType,
        filterText = this.text,
        ignoresCase = this.ignoreCase
    )
}
