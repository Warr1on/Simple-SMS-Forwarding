package ru.warr1on.simplesmsforwarding.domain.repositories

import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsRequest
import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsResponse
import ru.warr1on.simplesmsforwarding.data.remote.service.FwdbotService

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
        messageTypeKey: String,
        senderKey: String
    ): ForwardSmsResponse

    object Factory {

        fun getDefaultRepo(apiService: FwdbotService): FwdbotServiceRepository {
            return FwdbotServiceRepositoryImpl(apiService)
        }
    }
}

private class FwdbotServiceRepositoryImpl(
    private val apiService: FwdbotService
) : FwdbotServiceRepository {

    override suspend fun postSmsForwardingRequest(
        address: String,
        messageBody: String,
        messageTypeKey: String,
        senderKey: String
    ): ForwardSmsResponse {

        val request = ForwardSmsRequest(
            address = address,
            body = messageBody,
            senderKey = senderKey,
            messageTypeKey = messageTypeKey
        )

        return apiService.postSmsForwardingRequest(request)
    }
}