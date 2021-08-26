package com.cowpay.cowpaysdk.network.base

import com.cowpay.cowpaysdk.models.CowpayRequest
import com.cowpay.cowpaysdk.models.CowpayResponse
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    companion object {
        fun createAuthService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }

    }

    @POST("charge/fawry")
    suspend fun payWithFawry(@Body request : CowpayRequest): Response<CowpayResponse>

    @POST("charge/card/direct")
    suspend fun payWithCard(@Body request : CowpayRequest): Response<CowpayResponse>

}