package ru.warr1on.simplesmsforwarding.domain.services.messageForwarding

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.simplemobiletools.smsmessenger.R
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import ru.warr1on.simplesmsforwarding.domain.model.MessageForwardingRecord
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSettingsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.FwdbotServiceRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.MessageForwardingRecordsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.MessageForwardingRequestResult

/**
 * A helper DTO used to pass the necessary data to [MessageForwardingWorker].
 *
 * To create a [Data] object for a worker, use the instance method [toWorkerData] or the
 * object method [workerData], to get the data DTO in a worker, use [parseFromWorkerData]
 *
 * @property messageForwardingRecordID ID of the record stored in the database
 * @property           messageTypeKeys Message type keys of the rules that the message have passed
 */
class MessageForwardingWorkerData(
    val messageForwardingRecordID: String,
    val messageTypeKeys: List<String>
) {

    private object Keys {
        const val messageForwardingRecordID = "msg_record_id"
        const val messageTypeKeys = "msg_type_keys"
    }

    /**
     * Converts this instance to a [Data] object which
     * can be passed to a worker as its input data
     */
    fun toWorkerData(): Data {
        return workerData(this.messageForwardingRecordID, this.messageTypeKeys)
    }

    companion object {

        /**
         * Creates a [Data] object to pass to a [MessageForwardingWorker]
         *
         * @param messageForwardingRecordID ID of a forwarding record in a database
         * @param           messageTypeKeys A list of message type keys for the backend
         *                                  to determine what kind of messages it receives
         *                                  and to whom they should be forwarded to
         */
        fun workerData(
            messageForwardingRecordID: String,
            messageTypeKeys: List<String>
        ): Data {
            val workerData = Data.Builder()
            workerData.putString(Keys.messageForwardingRecordID, messageForwardingRecordID)
            workerData.putStringArray(Keys.messageTypeKeys, messageTypeKeys.toTypedArray())
            return workerData.build()
        }

        /**
         * Parses the passed [workerData] into a [MessageForwardingWorkerData] DTO
         * that allows easy access to the data that was received by the worker.
         *
         * @param workerData a [Data] object that is received by the worker
         */
        fun parseFromWorkerData(workerData: Data): MessageForwardingWorkerData? {
            val recordID = workerData.getString(Keys.messageForwardingRecordID)
            val messageTypeKeys = workerData.getStringArray(Keys.messageTypeKeys)
            return if (recordID != null && messageTypeKeys != null) {
                MessageForwardingWorkerData(
                    messageForwardingRecordID = recordID,
                    messageTypeKeys = messageTypeKeys.toList()
                )
            } else {
                null
            }
        }
    }
}

/**
 * A worker which job is to forward a message to a backend chat bot and correspondingly
 * update the message's forwarding record according to the result
 *
 * @param        recordsRepo A repo of message forwarding record. Used by the worker
 *                           to get the stored record of a message corresponding to
 *                           the ID passed in the input data
 * @param       settingsRepo A repo of app settings. Used to get the user's sender key,
 *                           which is used by the backend to determine from whom the
 *                           message was forwarded
 * @param backendServiceRepo A repo that communicates with the backend chat bot
 * @param            context Used in the superclass' constructor params
 * @param   workerParameters Used in the superclass' constructor params
 */
class MessageForwardingWorker(
    private val recordsRepo: MessageForwardingRecordsRepository,
    private val settingsRepo: ForwarderSettingsRepository,
    private val backendServiceRepo: FwdbotServiceRepository,
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        /**
         * Enqueues a new forwarding job using the provided [workManager] with the given [inputData]
         *
         * @param workManager A [WorkManager] instance that is used to enqueue the job
         * @param   inputData A [MessageForwardingWorkerData] object that contains the
         *                    necessary info to forward the message
         */
        fun enqueue(
            workManager: WorkManager,
            inputData: MessageForwardingWorkerData
        ) {
            val workRequest = OneTimeWorkRequestBuilder<MessageForwardingWorker>()
                .setInputData(inputData.toWorkerData())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .addTag(generateJobTag(inputData))
                .build()
            workManager.enqueue(workRequest)
        }

        private fun generateJobTag(inputData: MessageForwardingWorkerData): String {
            return "msg_forwarding_job#${inputData.messageForwardingRecordID}"
        }
    }

    override suspend fun doWork(): Result {

        // Retrieving input data as a typed DTO
        // and failing the job if failed to do so
        val workerData = MessageForwardingWorkerData
            .parseFromWorkerData(inputData) ?: return Result.failure()

        // Retrieving the message forwarding record and the sender key from the local storage
        val record = getMessageForwardingRecord(workerData.messageForwardingRecordID) ?: return Result.failure()
        val senderKey = getStoredSenderKey() ?: return Result.failure()
        // TODO: Make a proper handling of having multiple message type keys.
        //  The current implementation supports 1 key at max, as it's just what the current backend API supports.
        //  This limits forwarding capability so that a message can't be forwarded according to multiple rules
        //  simultaneously as one request. This would need to be fixed after the backend adapts according changes.
        val messageTypeKey = workerData.messageTypeKeys.first()

        // Making an API call to the chatbot backend to forward the message
        val forwardingResult = forwardMessage(
            messageForwardingRecord = record,
            messageTypeKey = messageTypeKey,
            senderKey = senderKey
        )

        // Mapping the result into a single unwrapped type
        val finalResult = forwardingResult.fold(
            onSuccess = { forwardingResult ->
                forwardingResult
            },
            onFailure = { error ->
                MessageForwardingRequestResult(
                    result = MessageForwardingRequestResult.Result.FAILURE,
                    resultDescription = error.localizedMessage ?: error.message ?: ""
                )
            }
        )

        // Updating the message forwarding record in the database
        // according to the result
        updateMessageForwardingRecord(record, finalResult)

        return Result.success()
    }

    /**
     * Retrieves the stored message forwarding record from the database by its [recordID].
     *
     * @return Optional - [MessageForwardingRecord] or null if the record wasn't found
     */
    private fun getMessageForwardingRecord(recordID: String): MessageForwardingRecord? {
        return try {
            recordsRepo.records.value.first { it.id == recordID }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /**
     * Retrieves the user's sender key from the local data store
     *
     * @return Optional - either the sender key string or null if failed to
     *         access the current app settings or the sender key isn't set
     */
    private suspend fun getStoredSenderKey(): String? {
        return try {
            settingsRepo.currentSettings.first()?.senderKey
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /**
     * Makes an API call to the chatbot backend to forward the message to the recipients
     *
     * @param messageForwardingRecord The record that is used to get the message address and body
     * @param messageTypeKey The key for the chatbot to identify the type of the message
     * @param senderKey The key to identify the sender of the forwarded message
     *
     * @return A [Result] that will contain [MessageForwardingRequestResult] if the operation
     *         was successful, or an exception that caused the failure otherwise
     */
    private suspend fun forwardMessage(
        messageForwardingRecord: MessageForwardingRecord,
        messageTypeKey: String,
        senderKey: String
    ): kotlin.Result<MessageForwardingRequestResult> {
        return try {
            val response = backendServiceRepo.postSmsForwardingRequest(
                address = messageForwardingRecord.messageAddress,
                messageBody = messageForwardingRecord.messageBody,
                messageTypeKey = messageTypeKey,
                senderKey = senderKey
            )
            kotlin.Result.success(response)
        } catch (e: HttpException) {
            kotlin.Result.failure(e)
        }
    }

    /**
     * Updates the message forwarding record in the database according to the forwarding result
     *
     * @param           record The original non-updated record
     * @param forwardingResult The result of forwarding operation
     */
    private suspend fun updateMessageForwardingRecord(
        record: MessageForwardingRecord,
        forwardingResult: MessageForwardingRequestResult
    ) {
        val isFulfilled = forwardingResult.result != MessageForwardingRequestResult.Result.FAILURE
        val updatedRecord = record.copy(
            isFulfilled = isFulfilled,
            resultDescription = forwardingResult.resultDescription
        )
        recordsRepo.updateRecord(updatedRecord)
    }


    //--- Android <12 support for expedited work ---//

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val context = applicationContext
        return ForegroundInfo(
            1,
            createNotification(context)
        )
    }

    private fun createNotification(context: Context): Notification {
        val channelId = "forwarding_channel_id"
        val channelName = "Forwarding Channel"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle("Forwarding message...")
            .setContentText("Sending received message to a chatbot")
            .setOngoing(true)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        return builder.build()
    }
}
