package ru.warr1on.simplesmsforwarding.domain.model

data class ForwardingRule(
    val typeKey: String,
    val allowedAddresses: List<String>,
    val filters: List<ForwardingFilter>
) {
    fun checkIfMessagePassesThisRule(message: SmsMessage): Boolean {
        try {
            allowedAddresses.first { it == message.address }
        } catch (e: NoSuchElementException) {
            return false
        }
        filters.forEach {
            if (!it.checkIfMessagePassesThisFilter(message)) {
                return false
            }
        }
        return true
    }
}
