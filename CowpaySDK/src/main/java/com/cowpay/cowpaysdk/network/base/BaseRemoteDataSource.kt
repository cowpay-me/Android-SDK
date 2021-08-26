package com.cowpay.cowpaysdk.network.base

import com.google.gson.Gson
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
abstract class BaseRemoteDataSource {

    private val gson = Gson()

    suspend fun <T> apiRequest(
        call: suspend () -> Response<T>, apiResponseCallbacks: BaseResponse<T>
    )  where T:ApiBaseResponse{

        try {
            val response = call.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (response.code() == 200 && body?.success == true)
                    apiResponseCallbacks.onResult(body)
                else
                    apiResponseCallbacks.onError(body?.error ?: "Something Wrong happened")
            } else {
                apiResponseCallbacks.onError(
                    gson.fromJson(
                        response.errorBody()?.string(),
                        ApiErrorResponse::class.java
                    ).error ?: ""
                )
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            return apiResponseCallbacks.onError(e.localizedMessage)
        }
    }

}