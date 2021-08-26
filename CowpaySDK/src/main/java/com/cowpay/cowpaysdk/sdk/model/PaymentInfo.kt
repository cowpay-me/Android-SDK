package com.cowpay.cowpaysdk.sdk.model

data class PaymentInfo(
    val merchantReferenceId: String ,
    var customerMerchantProfileId: String,
    val amount :Double ,
    val customerName:String,
    val customerMobile :String,
    val customerEmail :String,
    val description: String ,
)
