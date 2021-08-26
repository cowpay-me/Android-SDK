package com.cowpay.cowpaysdk.network.base

data class BaseResponse<T>(
    val onResult: (response: T?) -> Unit,
    val onError: (error: String) -> Unit,
)
