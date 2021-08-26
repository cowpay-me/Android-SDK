package com.cowpay.cowpaysdk.network.base

import com.google.gson.annotations.SerializedName

open class ApiBaseResponse (
    @SerializedName("status_description") val error: String? = "",
    @SerializedName("success") val success: Boolean? = false
)