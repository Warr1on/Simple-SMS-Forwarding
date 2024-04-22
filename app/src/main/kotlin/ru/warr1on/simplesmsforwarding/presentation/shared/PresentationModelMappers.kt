package ru.warr1on.simplesmsforwarding.presentation.shared

import ru.warr1on.simplesmsforwarding.domain.model.filtering.FilterType
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingFilter
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule

/**
 * Converts domain model [ForwardingRule] into presentation model [PresentationModel.ForwardingRule]
 */
fun ForwardingRule.toPresentationModel(): PresentationModel.ForwardingRule {
    return PresentationModel.ForwardingRule(
        id = this.id,
        name = this.name,
        typeKey = this.typeKey,
        applicableAddresses = this.applicableAddresses,
        filters = this.filters.map { it.toPresentationModel() }
    )
}

/**
 * Converts domain model [ForwardingFilter] into presentation model [PresentationModel.ForwardingFilter]
 */
fun ForwardingFilter.toPresentationModel(): PresentationModel.ForwardingFilter {
    return PresentationModel.ForwardingFilter(
        id = this.id,
        filterType = this.filterType.toPresentationModel(),
        text = this.text,
        ignoreCase = this.ignoreCase
    )
}

/**
 * Converts domain model [FilterType] into presentation model [PresentationModel.ForwardingFilter.FilterType]
 */
fun FilterType.toPresentationModel(): PresentationModel.ForwardingFilter.FilterType {
    return when (this) {
        FilterType.INCLUDE -> PresentationModel.ForwardingFilter.FilterType.INCLUDE
        FilterType.EXCLUDE -> PresentationModel.ForwardingFilter.FilterType.EXCLUDE
    }
}
