package ru.warr1on.simplesmsforwarding.domain.model.filtering

import ru.warr1on.simplesmsforwarding.domain.model.SmsMessage

enum class FilterType {
    INCLUDE, EXCLUDE
}

/**
 * A filter to determine if the specific message should be forwarded or not.
 *
 * @param filterType The type of the filter, i.e. inclusion or exclusion
 * @param       text A text value which should contain a key word or phrase to be used as a filter
 * @param ignoreCase An optional flag that determines if the original text case should be
 *                   respected when checking if the message text passes the filter
 */
data class ForwardingFilter(
    val id: String,
    val filterType: FilterType,
    val text: String,
    val ignoreCase: Boolean = false
) {
    fun checkIfMessagePassesThisFilter(message: SmsMessage): Boolean {
        val messageText = message.body
        return if (filterType == FilterType.INCLUDE) {
            messageText.contains(text, ignoreCase)
        } else {
            !messageText.contains(text, ignoreCase)
        }
    }

    companion object {

        fun testVariant1(): ForwardingFilter {
            return ForwardingFilter(
                id = "id1",
                filterType = FilterType.INCLUDE,
                text = "Your auth code is",
                ignoreCase = false
            )
        }

        fun testVariant2(): ForwardingFilter {
            return ForwardingFilter(
                id = "id2",
                filterType = FilterType.EXCLUDE,
                text = "This should be ignored",
                ignoreCase = true
            )
        }

        fun testVariant3(): ForwardingFilter {
            return ForwardingFilter(
                id = "id3",
                filterType = FilterType.INCLUDE,
                text = "Important message that should be forwarded",
                ignoreCase = true
            )
        }
    }
}
