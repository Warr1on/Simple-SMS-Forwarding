package ru.warr1on.simplesmsforwarding.domain.model

enum class FilterType {
    INCLUDE, EXCLUDE
}

/**
 * A filter to determine if the specific message should be forwarded or not.
 *
 * @param filterType The type of the filter, i.e. inclusion or exclusion
 * @param text A text value which should contain key word or phrase to be used as a filter
 * @param ignoreCase An optional flag that determines if the original text case should be respected
 */
data class ForwardingFilter(
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
}
