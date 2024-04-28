package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.model

sealed class MessageForwardingState {

    data object CurrentlyForwarding : MessageForwardingState()
    data class FailedToForward(
        val reasonOfFailure: String
    ) : MessageForwardingState()
    data class PartialSuccess(
        val description: String
    ) : MessageForwardingState()
    data class ForwardedSuccessfully(
        val numberOfRecipients: Int,
        val recipientNames: List<String>
    ) : MessageForwardingState()
}
