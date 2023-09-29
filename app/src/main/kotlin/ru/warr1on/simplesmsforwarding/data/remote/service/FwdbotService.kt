package ru.warr1on.simplesmsforwarding.data.remote.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.warr1on.simplesmsforwarding.TestConstants
import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsRequest
import ru.warr1on.simplesmsforwarding.data.remote.dto.ForwardSmsResponse
import java.util.concurrent.TimeUnit

interface FwdbotService {

    @Headers("Content-Type:application/json")
    @POST("forward/sms")
    suspend fun postSmsForwardingRequest(@Body request: ForwardSmsRequest): ForwardSmsResponse

    companion object {

        private var sharedService: FwdbotService? = null

        fun get(): FwdbotService {
            var service = sharedService
            return if (service != null) {
                service
            } else {
                service = createService()
                sharedService = service
                service
            }
        }

        private fun createService(): FwdbotService {
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build()

            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(TestConstants.fwdbotBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(FwdbotService::class.java)
        }
    }
}
