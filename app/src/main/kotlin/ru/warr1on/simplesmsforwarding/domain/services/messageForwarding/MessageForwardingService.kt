package ru.warr1on.simplesmsforwarding.domain.services.messageForwarding

import androidx.work.WorkManager

/**
 * MessageForwardingService serves as an entry point for chatbot forwarding functionality.
 * Its responsibility lies in receiving the incoming message and then starting a chain of work
 * that then would check if the message passes the user-defined rules, and, if necessary, create
 * a new message forwarding record in the database and then forward the message
 */
class MessageForwardingService(private val workManager: WorkManager) {

    /**
     * This should be called whenever a new SMS is received by the app
     * to notify [MessageForwardingService] of it, so that it would take
     * according actions to register and forward the message if necessary.
     */
    fun handleReceivedSms(
        messageAddress: String,
        messageBody: String
    ) {
        val messageData = MessageInitialProcessingWorkerData(messageAddress, messageBody)
        MessageInitialProcessingWorker.enqueue(workManager, messageData)
    }
}

