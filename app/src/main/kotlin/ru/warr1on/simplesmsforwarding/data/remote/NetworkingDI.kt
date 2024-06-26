package ru.warr1on.simplesmsforwarding.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.warr1on.simplesmsforwarding.TestConstants
import ru.warr1on.simplesmsforwarding.core.di.DI
import ru.warr1on.simplesmsforwarding.data.remote.api.FwdbotApi
import java.util.concurrent.TimeUnit

object NetworkingQualifiers {
    const val loggingInterceptor = "logging_interceptor"
}

val DI.Qualifiers.Networking: NetworkingQualifiers
    get() = NetworkingQualifiers

val DI.Modules.networking: Module
    get() {

        val qualifiers = DI.Qualifiers.Networking

        return module {

            // Retrofit API
            single {
                val retrofit = get<Retrofit>()
                return@single retrofit.create(FwdbotApi::class.java)
            }

            // Retrofit instance
            single {
                return@single Retrofit.Builder()
                    .client(get())
                    .baseUrl(TestConstants.fwdbotBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            // HTTP client
            factory {
                return@factory OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor = get(named(qualifiers.loggingInterceptor)))
                    .build()
            }

            // Logger
            factory {
                return@factory HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            } withOptions {
                named(qualifiers.loggingInterceptor)
            }
        }
    }
