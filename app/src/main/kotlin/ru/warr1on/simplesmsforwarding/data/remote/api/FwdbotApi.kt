package ru.warr1on.simplesmsforwarding.data.remote.api

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

interface FwdbotApi {

    @Headers("Content-Type:application/json")
    @POST("forward/sms")
    suspend fun postSmsForwardingRequest(@Body request: ForwardSmsRequest): ForwardSmsResponse

    // TODO: remove this part after migration to DI-provided networking dependencies
    companion object {

        private var sharedService: FwdbotApi? = null

        fun get(): FwdbotApi {
            var service = sharedService
            return if (service != null) {
                service
            } else {
                service = createService()
                sharedService = service
                service
            }
        }

        private fun createService(): FwdbotApi {
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

            return retrofit.create(FwdbotApi::class.java)
        }
    }
}
