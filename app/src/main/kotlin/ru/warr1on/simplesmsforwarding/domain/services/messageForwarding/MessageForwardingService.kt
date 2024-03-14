package ru.warr1on.simplesmsforwarding.domain.services.messageForwarding

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.simplemobiletools.smsmessenger.R
import kotlinx.coroutines.*
import retrofit2.HttpException
import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsRequest
import ru.warr1on.simplesmsforwarding.data.remote.api.FwdbotApi
import ru.warr1on.simplesmsforwarding.domain.model.SmsMessage
import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSettingsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.FwdbotServiceRepository
import java.util.UUID

class MessageForwardingService(
    //private val recordsRepo: MessageForwardingRecordsRepository,
    private val rulesRepo: ForwardingRulesRepository,
    private val settingsRepo: ForwarderSettingsRepository,
    //private val backendServiceRepo: FwdbotServiceRepository,
    //private val appContext: Context,
    private val coroutineScope: CoroutineScope
) {

    private var forwardingRules: List<ForwardingRule> = emptyList()

    init {
        coroutineScope.launch {
            rulesRepo.forwardingRules.collect {
                forwardingRules = it
            }
        }
    }

    fun handleReceivedSms(
        address: String,
        body: String,
        context: Context
    ) {
        val message = SmsMessage(address, body)
        val forwardingList = forwardingListFor(message)
        val senderKey = settingsRepo.currentSettings.value?.senderKey

        if (senderKey != null) {
            val messageTypeKeys = forwardingList.map { it.typeKey }
        } else {
            // TODO: Handle the case of missing the sender key
        }
    }

    private fun forwardingListFor(message: SmsMessage): List<ForwardingRule> {
        return forwardingRules.filter {
            it.checkIfMessagePassesThisRule(message)
        }
    }

    private fun scheduleForwardingTask(
        address: String,
        body: String,
        senderKey: String,
        messageTypeKeys: List<String>,
        context: Context
    ) {
        val taskID = UUID.randomUUID().toString()
        val inputData = packForwardingInfoIntoData(address, body, senderKey, messageTypeKeys)
        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<MessageForwardingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .addTag("smsForwardingWork#$taskID")
            .setInputData(inputData)
            .build()
        workManager.enqueue(workRequest)
    }

//    private fun createForwardingRecord(message: SmsMessage) {
//        val record = MessageForwardingRecord(
//            id = UUID.randomUUID().toString(),
//            messageAddress = message.address,
//            messageBody = message.body,
//            isFulfilled = false,
//            resultDescription = ""
//        )
//        coroutineScope.launch { recordsRepo.addRecord(record) }
//    }

    private fun packForwardingInfoIntoData(
        address: String,
        body: String,
        senderKey: String,
        messageTypeKeys: List<String>
    ): Data {
        val data = Data.Builder()
        data.putString("address", address)
        data.putString("body", body)
        data.putString("sender_key", senderKey)
        data.putStringArray("msgtype_keys", messageTypeKeys.toTypedArray())
        return data.build()
    }
}

class MessageForwardingWorker(
    val backendServiceRepo: FwdbotServiceRepository,
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val remoteService = FwdbotApi.get()
        val request = prepareRequestFromData(inputData) ?: return Result.failure()
        try {
            val response = remoteService.postSmsForwardingRequest(request)
            println("Got forward request response:\nResult: ${response.result}\nDescr: ${response.resultDescription}")
        } catch (e: HttpException) {
            return Result.failure()
        }
        return Result.success()
    }

    private fun prepareRequestsFromData(data: Data): List<ForwardSmsRequest> {
        val messageAddress = data.getString("address")
        val messageBody = data.getString("body")
        val senderKey = data.getString("sender_key")
        val messageTypeKeys = data.getStringArray("msgtype_keys")?.toList()

        return if (
            messageAddress != null &&
            messageBody != null &&
            senderKey != null &&
            messageTypeKeys != null
        ) {
            val requests = mutableListOf<ForwardSmsRequest>()
            messageTypeKeys.forEach {
                requests.add(
                    ForwardSmsRequest(
                        address = messageAddress,
                        body = messageBody,
                        senderKey = senderKey,
                        messageTypeKey = it
                    )
                )
            }
            requests
        } else {
            emptyList()
        }
    }

    private fun prepareRequestFromData(data: Data): ForwardSmsRequest? {
        val messageAddress = data.getString("address")
        val messageBody = data.getString("body")
        val senderKey = data.getString("sender_key")
        val messageTypeKey = data.getString("msgtype_key")

        return if (messageAddress != null &&
            messageBody != null &&
            senderKey != null &&
            messageTypeKey != null) {
            ForwardSmsRequest(
                address = messageAddress,
                body = messageBody,
                senderKey = senderKey,
                messageTypeKey = messageTypeKey
            )
        } else {
            null
        }
    }

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
