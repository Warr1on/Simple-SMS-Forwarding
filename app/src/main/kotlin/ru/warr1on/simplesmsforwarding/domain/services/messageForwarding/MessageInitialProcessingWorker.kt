package ru.warr1on.simplesmsforwarding.domain.services.messageForwarding

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import ru.warr1on.simplesmsforwarding.domain.model.MessageForwardingRecord
import ru.warr1on.simplesmsforwarding.domain.model.MessageForwardingRequestStatus
import ru.warr1on.simplesmsforwarding.domain.model.SmsMessage
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSettingsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.MessageForwardingRecordsRepository
import java.util.UUID

/**
 * A DTO that represents the input data that is needed by [MessageInitialProcessingWorker]
 *
 * @property messageAddress The address from which the message came. Usually represented by a phone number
 * @property    messageBody Text of the message
 */
data class MessageInitialProcessingWorkerData(
    val messageAddress: String,
    val messageBody: String
) {

    object Keys {
        const val messageAddress = "msg_address"
        const val messageBody = "msg_body"
    }

    /**
     * Converts this instance to a [Data] object which is used to
     * pass the necessary info about the message to a worker
     *
     * @return A [Data] object to be passed to a [MessageInitialProcessingWorker]
     */
    fun toWorkerData(): Data {
        return workerDataFromMessageContents(this.messageAddress, this.messageBody)
    }

    companion object {

        fun workerDataFromMessageContents(
            messageAddress: String,
            messageBody: String,
        ): Data {
            val workerData = Data.Builder()
            workerData.putString(Keys.messageAddress, messageAddress)
            workerData.putString(Keys.messageBody, messageBody)
            return workerData.build()
        }

        fun smsMessageFromWorkerData(workerData: Data): SmsMessage? {
            val messageAddress = workerData.getString(Keys.messageAddress)
            val messageBody = workerData.getString(Keys.messageBody)
            return if (messageAddress != null && messageBody != null) {
                SmsMessage(
                    address = messageAddress,
                    body = messageBody
                )
            } else {
                null
            }
        }
    }
}

/**
 * A worker which job is to process the incoming message: determine if it should be
 * forwarded, and, if so, add a corresponding record into db and schedule a forwarding task.
 *
 * @param        rulesRepo Forwarding rules repository. Used to get user's rules, that are then used
 *                         to determine a list of rules that the incoming message passes, which would
 *                         then qualify or disqualify the message as one that needs to be forwarded.
 * @param      recordsRepo Message forwarding records repo. Used to add a new record entry if the
 *                         received message is qualified for forwarding
 * @param     settingsRepo A repo for accessing the app settings. It's needed to get the sender key of the user
 * @param      workManager A WorkManager instance that would be used to schedule forwarding job(s)
 *                         when the message needs to be forwarded.
 * @param          context Used by the superclass' constructor
 * @param workerParameters Used by the superclass' constructor
 */
class MessageInitialProcessingWorker(
    private val rulesRepo: ForwardingRulesRepository,
    private val recordsRepo: MessageForwardingRecordsRepository,
    private val settingsRepo: ForwarderSettingsRepository,
    private val workManager: WorkManager,
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        fun enqueue(
            workManager: WorkManager,
            inputData: MessageInitialProcessingWorkerData
        ) {
            val workRequest = OneTimeWorkRequestBuilder<MessageInitialProcessingWorker>()
                .setInputData(inputData.toWorkerData())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .addTag(generateJobTag(inputData))
                .build()
            workManager.enqueue(workRequest)
        }

        private fun generateJobTag(inputData: MessageInitialProcessingWorkerData): String {
            val messageHash = (inputData.messageAddress + inputData.messageBody).hashCode()
            return "msg_initial_processing#${messageHash}#${UUID.randomUUID()}"
        }
    }

    override suspend fun doWork(): Result {

        // First, getting the message from the worker's input data
        val message = MessageInitialProcessingWorkerData
            .smsMessageFromWorkerData(inputData) ?: return Result.failure()

        // Then determining the forwarding list for this message
        val forwardingList = forwardingListFor(message)
        // If the forwarding list is empty, that means that
        // no additional work is required and the message
        // does not to be forwarded according to the rules
        // set up by the user, so the job can be successfully
        // finished at this point.
        if (forwardingList.isEmpty()) {
            return Result.success()
        }

        // If the message is qualified as such that needs to be forwarded,
        // firstly, a new forwarding record should be registered in db.
        val recordID = createNewForwardingRecordEntry(message)

        // Finally, enqueuing a worker that would actually forward the message
        scheduleForwardingJob(recordID, forwardingList)

        return Result.success()
    }

    /**
     * Determines a forwarding list for a received message.
     * A forwarding list is a list of forwarding rules,
     * filters of which are passed by the message.
     */
    private suspend fun forwardingListFor(message: SmsMessage): List<ForwardingRule> {
        val rules: List<ForwardingRule> = try {
            rulesRepo.forwardingRules.first()
        } catch (e: NoSuchElementException) {
            emptyList()
        }
        return rules.filter {
            it.checkIfMessagePassesThisRule(message)
        }
    }

    /**
     * Creates a new forwarding record entry in the database
     *
     * @return ID of the created record entry
     */
    private suspend fun createNewForwardingRecordEntry(message: SmsMessage): String {
        val recordID = UUID.randomUUID().toString()
        val record = MessageForwardingRecord(
            id = recordID,
            messageAddress = message.address,
            messageBody = message.body,
            status = MessageForwardingRequestStatus.PENDING,
            resultDescription = ""
        )
        recordsRepo.addRecord(record)
        return recordID
    }

    /**
     * Enqueues a new work request to forward the message
     *
     * @param forwardingRecordEntryID ID of the forwarding record in the database
     * @param          forwardingList List of all the forwarding rules that the message passes
     */
    private fun scheduleForwardingJob(
        forwardingRecordEntryID: String,
        forwardingList: List<ForwardingRule>
    ) {
        val workerData = MessageForwardingWorkerData(
            messageForwardingRecordID = forwardingRecordEntryID,
            messageTypeKeys = forwardingList.map { it.typeKey }
        )
        MessageForwardingWorker.enqueue(workManager, workerData)
    }
}
