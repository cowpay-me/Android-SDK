package com.cowpay.cowpaysdk.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CashCollection(
    val paymentGatewayReferenceId: String? = "",
    val merchantReferenceId: String? = "",
    val cowpayReferenceId: String? = ""
):Parcelable
