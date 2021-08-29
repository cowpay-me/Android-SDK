package com.cowpay.cowpaysdk.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardRequest(
    val name: String = "",
    val number: String = "",
    val date: String = "",
    val cvv: String = ""
):Parcelable
