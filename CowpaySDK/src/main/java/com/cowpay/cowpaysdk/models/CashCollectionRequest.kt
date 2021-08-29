package com.cowpay.cowpaysdk.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CashCollectionRequest(
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val address: String = "",
    val floor: String = "",
    val district: String = "",
    val apartment: String = "",
    val cityIndex: Int = 0
):Parcelable
