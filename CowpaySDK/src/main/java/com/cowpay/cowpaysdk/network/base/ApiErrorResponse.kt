package com.cowpay.cowpaysdk.network.base

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse (
    @SerializedName("status_description") val error: String? = ""
)
