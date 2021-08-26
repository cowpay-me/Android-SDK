package com.cowpay.cowpay.network.base

import com.cowpay.cowpaysdk.sdk.CowpaySDK
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val READ_TIME_OUT_CONNECTION = 1
private const val WRITE_TIME_OUT_CONNECTION = 1
private const val TIME_OUT_CONNECTION = 1
private val TINE_UNIT_FOR_CONNECTION = TimeUnit.MINUTES

object RetroClient {
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CowpaySDK.getUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .readTimeout(
                READ_TIME_OUT_CONNECTION.toLong(),
                TINE_UNIT_FOR_CONNECTION
            )
            .writeTimeout(
                WRITE_TIME_OUT_CONNECTION.toLong(),
                TINE_UNIT_FOR_CONNECTION
            )
            .connectTimeout(
                TIME_OUT_CONNECTION.toLong(),
                TINE_UNIT_FOR_CONNECTION
            )

            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    HttpLoggingInterceptor.Level.BODY
            }).build()
    }
}

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()

        val builder = req.newBuilder()
        if (CowpaySDK.token.isNotEmpty()) {
            builder.addHeader("Authorization", "Bearer ${CowpaySDK.token}")
        }
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        req = builder.build()
        return chain.proceed(req)
    }
}