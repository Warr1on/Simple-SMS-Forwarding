package ru.warr1on.simplesmsforwarding.domain.repositories

import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsRequest
import ru.warr1on.simplesmsforwarding.data.remote.api.FwdbotApi
import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsResponse

data class MessageForwardingRequestResult(
    val result: Result,
    val resultDescription: String,
    val numberOfRecipients: Int? = null,
    val recipients: List<String>? = null
) {
    enum class Result(val stringRepresentation: String) {
        SUCCESS("success"),
        PARTIAL_SUCCESS("partial_success"),
        FAILURE("failure"),
        UNDEFINED("undefined")
    }

    companion object {

        fun typedResultFromString(resultAsString: String): Result {
            return when (resultAsString) {
                Result.SUCCESS.stringRepresentation -> Result.SUCCESS
                Result.PARTIAL_SUCCESS.stringRepresentation -> Result.PARTIAL_SUCCESS
                Result.FAILURE.stringRepresentation -> Result.FAILURE
                else -> Result.UNDEFINED
            }
        }
    }
}

private fun ForwardSmsResponse.toMessageForwardingResult(): MessageForwardingRequestResult {
    return MessageForwardingRequestResult(
        result = MessageForwardingRequestResult.typedResultFromString(this.result),
        resultDescription = this.resultDescription,
        numberOfRecipients = this.numberOfRecipients,
        recipients = recipients
    )
}

/**
 * This is a repo that allows the app to interact with the actual forwarding bot on the backend.
 */
interface FwdbotServiceRepository {

    /**
     * Posts a new forwarding requests to the backend forwarding bot.
     *
     * The general flow of things is that the client posts a new forwarding
     * request to the backend bot, then the bot will decide (based on the
     * sender key and message type key) to whom the message should be forwarded
     * to, then the bot would forward the message to the recipients and return
     * a reply with the result of the operation.
     */
    suspend fun postSmsForwardingRequest(
        address: String,
        messageBody: String,
        messageTypeKeys: List<String>,
        senderKey: String
    ): MessageForwardingRequestResult

    object Factory {

        fun getDefaultRepo(apiService: FwdbotApi): FwdbotServiceRepository {
            return FwdbotServiceRepositoryImpl(apiService)
        }
    }
}

private class FwdbotServiceRepositoryImpl(
    private val apiService: FwdbotApi
) : FwdbotServiceRepository {

    override suspend fun postSmsForwardingRequest(
        address: String,
        messageBody: String,
        messageTypeKeys: List<String>,
        senderKey: String
    ): MessageForwardingRequestResult {

        val request = ForwardSmsRequest(
            address = address,
            body = messageBody,
            senderKey = senderKey,
            messageTypeKeys = messageTypeKeys
        )

        val rawResult = apiService.postSmsForwardingRequest(request)
        return rawResult.toMessageForwardingResult()
    }
}
