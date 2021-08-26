package com.cowpay.cowpaysdk.models

import com.google.gson.annotations.SerializedName

open class CowpayRequest {
    @SerializedName("merchant_reference_id")
    internal var merchantReferenceId: String? = null

    @SerializedName("customer_merchant_profile_id")
    internal var customerMerchantProfileId: String? = null

    @SerializedName("customer_name")
    internal var customerName: String? = null

    @SerializedName("customer_email")
    internal var customerEmail: String? = null

    @SerializedName("customer_mobile")
    internal var customerMobile: String? = null

    @SerializedName("amount")
    internal var amount: String? = null

    @SerializedName("signature")
    internal var signature: String? = null

    @SerializedName("description")
    internal var description: String? = null

    @SerializedName("card_number")
    internal var cardNumber: String? = null

    @SerializedName("cvv")
    internal var cvv: String? = null

    @SerializedName("expiry_year")
    internal var expiryYear: String? = null

    @SerializedName("expiry_month")
    internal var expiryMonth: String? = null

    @SerializedName("card_holder")
    internal var cardHolder: String? = null
}