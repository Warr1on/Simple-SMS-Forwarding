package ru.warr1on.simplesmsforwarding.domain.mapping

import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingFilter
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedForwardingFilter
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedForwardingRule
import ru.warr1on.simplesmsforwarding.data.local.model.PersistedRuleAssociatedPhoneAddress

fun PersistedForwardingRule.mapToForwardingRule(): ForwardingRule {
    return ForwardingRule(
        id = this.ruleDescription.id,
        name = this.ruleDescription.name,
        typeKey = this.ruleDescription.typeKey,
        applicableAddresses = this.applicableAddresses.map { it.mapToPhoneAddressString() },
        filters = this.filters.map { it.mapToForwardingFilter() }
    )
}

fun PersistedForwardingFilter.mapToForwardingFilter(): ForwardingFilter {

    val filterType = when (this.filterType) {
        PersistedForwardingFilter.Values.filterTypeInclude -> FilterType.INCLUDE
        PersistedForwardingFilter.Values.filterTypeExclude -> FilterType.EXCLUDE
        else -> FilterType.INCLUDE
    }

    return ForwardingFilter(
        id = this.id,
        filterType = filterType,
        text = this.filterText,
        ignoreCase = this.ignoresCase
    )
}

fun PersistedRuleAssociatedPhoneAddress.mapToPhoneAddressString(): String {
    return this.address
}
