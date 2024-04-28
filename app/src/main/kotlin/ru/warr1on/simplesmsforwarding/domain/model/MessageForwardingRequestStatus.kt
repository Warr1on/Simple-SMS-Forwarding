package ru.warr1on.simplesmsforwarding.domain.model

/**
 * Status of an SMS forwarding request.
 *
 * Used to determine if the message was forwarded or not yet,
 * if the bot backend fulfilled the request, and if it did so
 * successfully or only with partial success
 */
enum class MessageForwardingRequestStatus {

    /**
     * The message is registered as such that needs
     * to be forwarded, and awaits forwarding
     */
    PENDING,

    /** The message was successfully forwarded */
    SUCCESS,

    /**
     * The message was forwarded, but there were some issues and
     * the backend was unable to fulfill the request fully.
     *
     * For example, the bot backend successfully received SMS and was
     * able to forward it to some recipients but not all of them.
     */
    PARTIAL_SUCCESS,

    /** The message forwarding failed */
    FAILURE
}
