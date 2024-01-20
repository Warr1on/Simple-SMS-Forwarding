package ru.warr1on.simplesmsforwarding.domain.model

/**
 * Status of an SMS forwarding request.
 *
 * Used to determine if the bot backend fulfilled the request
 * and if it did so successfully or only with partial success;
 * or
 */
enum class MessageForwardingRequestStatus {

    /** Request was fulfilled completely */
    SUCCESS,

    /**
     * Request was fulfilled only partially.
     *
     * For example, the bot backend successfully received SMS and was
     * able to forward it to some recipients but not all of them.
     */
    PARTIAL_SUCCESS,

    /** Request wasn't fulfilled */
    FAILURE
}
