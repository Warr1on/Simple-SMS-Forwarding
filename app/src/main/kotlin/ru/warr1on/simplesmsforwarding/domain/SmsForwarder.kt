package ru.warr1on.simplesmsforwarding.domain

//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.pm.ServiceInfo
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.work.*
//import com.simplemobiletools.smsmessenger.R
//import retrofit2.HttpException
//import ru.warr1on.simplesmsforwarding.TestConstants
//import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsRequest
//import ru.warr1on.simplesmsforwarding.data.remote.api.FwdbotApi
//import ru.warr1on.simplesmsforwarding.domain.model.filtering.ForwardingRule
//import ru.warr1on.simplesmsforwarding.domain.model.SmsMessage
//import java.util.UUID

//object SmsForwarder {
//
//    fun handleReceivedSms(
//        address: String,
//        body: String,
//        context: Context
//    ) {
//        val message = SmsMessage(address, body)
//        val forwardingList = forwardingListFor(message)
//
//        forwardingList.forEach {
//            scheduleForwardingTask(
//                address = address,
//                body = body,
//                senderKey = TestConstants.senderKey,
//                messageTypeKey = it.typeKey,
//                context = context
//            )
//        }
//    }
//
//    private fun forwardingListFor(message: SmsMessage): List<ForwardingRule> {
//        val forwardingRules = TestConstants.forwardingRules
//        return forwardingRules.filter {
//            it.checkIfMessagePassesThisRule(message)
//        }
//    }
//
//    private fun scheduleForwardingTask(
//        address: String,
//        body: String,
//        senderKey: String,
//        messageTypeKey: String,
//        context: Context
//    ) {
//        val taskID = UUID.randomUUID().toString()
//        val inputData = packForwardingInfoIntoData(address, body, senderKey, messageTypeKey)
//        val workManager = WorkManager.getInstance(context)
//        val workRequest = OneTimeWorkRequestBuilder<ForwardSmsWorker>()
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
//            .addTag("smsForwardingWork#$taskID")
//            .setInputData(inputData)
//            .build()
//        workManager.enqueue(workRequest)
//    }
//
//    private fun packForwardingInfoIntoData(
//        address: String,
//        body: String,
//        senderKey: String,
//        messageTypeKey: String
//    ): Data {
//        val data = Data.Builder()
//        data.putString("address", address)
//        data.putString("body", body)
//        data.putString("sender_key", senderKey)
//        data.putString("msgtype_key", messageTypeKey)
//        return data.build()
//    }
//}
//
//class ForwardSmsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        val remoteService = FwdbotApi.get()
//        val request = prepareRequestFromData(inputData) ?: return Result.failure()
//        try {
//            val response = remoteService.postSmsForwardingRequest(request)
//            println("Got forward request response:\nResult: ${response.result}\nDescr: ${response.resultDescription}")
//        } catch (e: HttpException) {
//            return Result.failure()
//        }
//        return Result.success()
//    }
//
//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        val context = applicationContext
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            ForegroundInfo(
//                1,
//                createNotification(context),
//                ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
//            )
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ForegroundInfo(
//                1,
//                createNotification(context),
//                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
//            )
//        } else {
//            ForegroundInfo(
//                1,
//                createNotification(context)
//            )
//        }
//    }
//
//    private fun createNotification(context: Context): Notification {
//        val channelId = "forwarding_channel_id"
//        val channelName = "Forwarding Channel"
//
//        val builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.ic_launcher_monochrome)
//            .setContentTitle("Forwarding message...")
//            .setContentText("Sending received message to a chatbot")
//            .setOngoing(true)
//            .setAutoCancel(true)
//
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        return builder.build()
//    }
//
//    private fun prepareRequestFromData(data: Data): ForwardSmsRequest? {
//        val messageAddress = data.getString("address")
//        val messageBody = data.getString("body")
//        val senderKey = data.getString("sender_key")
//        val messageTypeKey = data.getString("msgtype_key")
//
//        return if (messageAddress != null &&
//            messageBody != null &&
//            senderKey != null &&
//            messageTypeKey != null) {
//            ForwardSmsRequest(
//                address = messageAddress,
//                body = messageBody,
//                senderKey = senderKey,
//                messageTypeKey = messageTypeKey
//            )
//        } else {
//            null
//        }
//    }
//}
