package ru.warr1on.simplesmsforwarding.presentation.shared

import androidx.compose.runtime.Immutable

/**
 * A namespace specific to presentation models
 */
interface PresentationModel {

    /**
     * A presentation model for a forwarding filter
     *
     * @param filterType The type of the filter, i.e. inclusion or exclusion
     * @param       text A text value which should contain a key word or phrase to be used as a filter
     * @param ignoreCase An optional flag that determines if the original text case should be
     *                   respected when checking if the message text passes the filter
     */
    @Immutable
    data class ForwardingFilter(
        val id: String,
        val filterType: FilterType,
        val text: String,
        val ignoreCase: Boolean
    ) {
        enum class FilterType {
            INCLUDE, EXCLUDE
        }
    }

    /**
     * A presentation model of a forwarding rule.
     *
     * Forwarding rules are a combination of filters that are used to determine if
     * the specific message should be forwarded, and addresses that a rule is applied to.
     *
     * @param                  id Unique identifier of this rule
     * @param                name Rule's name. Used for user's convenience
     * @param             typeKey A unique key that is used to identify the type of a forwarded message
     *                            on the backend service, so that it would know what kind of message it
     *                            received and to whom it should forward the message to.
     * @param applicableAddresses A list of addresses that this rule applies to. If the message received
     *                            comes from such an address, it will be checked on if it passes the filters
     *                            of this rule, and, if so, it will be sent to the backend to be forwarded
     * @param             filters A list of filters that are used to determine if a message passes this rule
     */
    @Immutable
    data class ForwardingRule(
        val id: String,
        val name: String,
        val typeKey: String,
        val applicableAddresses: List<String>,
        val filters: List<ForwardingFilter>
    )
}
