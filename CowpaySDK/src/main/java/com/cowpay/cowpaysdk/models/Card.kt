package com.cowpay.cowpaysdk.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(
    val paymentGatewayReferenceId: String? = "",
    val cowpayReferenceId: String? = ""
):Parcelable
