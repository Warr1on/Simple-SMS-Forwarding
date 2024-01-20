package ru.warr1on.simplesmsforwarding.domain.model.filtering

import ru.warr1on.simplesmsforwarding.domain.model.SmsMessage

/**
 * A forwarding rule.
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
data class ForwardingRule(
    val id: String,
    val name: String,
    val typeKey: String,
    val applicableAddresses: List<String>,
    val filters: List<ForwardingFilter>
) {
    fun checkIfMessagePassesThisRule(message: SmsMessage): Boolean {
        try {
            applicableAddresses.first { it == message.address }
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

    companion object {

        fun testVariant1(): ForwardingRule {
            return ForwardingRule(
                id = "123456",
                name = "Test rule",
                typeKey = "msgtype_1",
                applicableAddresses = listOf(
                    "89335447540",
                    "900",
                    "ExampleCompany"
                ),
                filters = listOf(
                    ForwardingFilter.testVariant1(),
                    ForwardingFilter.testVariant2(),
                    ForwardingFilter.testVariant3()
                )
            )
        }
    }
}
