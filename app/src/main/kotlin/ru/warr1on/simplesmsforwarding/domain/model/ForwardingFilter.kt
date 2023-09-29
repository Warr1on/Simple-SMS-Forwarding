package ru.warr1on.simplesmsforwarding.domain.model

enum class FilterType {
    INCLUDE, EXCLUDE
}
data class ForwardingFilter(
    val filterType: FilterType,
    val text: String
) {
    fun checkIfMessagePassesThisFilter(message: SmsMessage): Boolean {
        val messageText = message.body
        return if (filterType == FilterType.INCLUDE) {
            messageText.contains(text)
        } else {
            !messageText.contains(text)
        }
    }
}
