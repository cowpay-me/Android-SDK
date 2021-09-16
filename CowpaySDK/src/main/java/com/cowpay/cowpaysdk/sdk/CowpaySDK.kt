package com.cowpay.cowpaysdk.sdk

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.cowpay.cowpaysdk.sdk.model.PaymentInfo
import com.cowpay.cowpaysdk.ui.activity.home.HomeCopwayActivity
import java.security.MessageDigest

object CowpaySDK {
    // this info related to organization
    internal var token = ""
    private var merchantCode = ""
    private var hashKey = ""

    // this info related to every order
    internal var paymentInfo: PaymentInfo? = null
    internal var signature = ""
    private var enviroment = CowpayEnviroment.STAGING
    internal var paymentMethodAvailability = arrayOf(CowpaySDKPaymentMethod.CARD,CowpaySDKPaymentMethod.FAWRY,CowpaySDKPaymentMethod.CASH_COLLECTION)
    internal var callback: CowpayCallback? = null
    internal var successMsgForCashCollection: String? = null


    internal fun getUrl(): String {
        return if (enviroment == CowpayEnviroment.STAGING)
            "https://staging.cowpay.me/api/v2/"
        else "https://cowpay.me/api/v2/"
    }

    internal fun getUrlWebView(): String {
        return if (enviroment == CowpayEnviroment.STAGING)
            "https://staging.cowpay.me/v2/card/form/"
        else "https://cowpay.me/v2/card/form/"
    }

    fun init(
        token: String,
        merchantCode: String,
        hashKey: String,
        environment: CowpayEnviroment = CowpayEnviroment.STAGING
    ) {
        this.enviroment = environment
        this.token = token
        this.merchantCode = merchantCode
        this.hashKey = hashKey
    }

    fun setPaymentMethodAvailability(array: Array<CowpaySDKPaymentMethod>) {
        this.paymentMethodAvailability = array
    }

    fun setSuccessMsgForCashCollection(str:String) {
        this.successMsgForCashCollection = successMsgForCashCollection
    }

    fun launch(
        activity: FragmentActivity,
        paymentInfo: PaymentInfo,
        callback: CowpayCallback? = null
    ) {
        if (token.isEmpty())
            throw Exception("Invalid Access Token")
        if (merchantCode.isEmpty())
            throw Exception("Invalid Merchant Code")
        if (hashKey.isEmpty())
            throw Exception("Invalid Hash Key")
        this.paymentInfo = paymentInfo
        this.callback = callback
        this.signature = generateSignature()
//        if (screenMode == CowpayScreenMode.OVERLAY)
//            PayDialogFragment().show(activity.supportFragmentManager, "bottom")
//        else
            activity.startActivity(Intent(activity, HomeCopwayActivity::class.java))
    }

    private fun generateSignature() :String {
        val str =
            "$merchantCode${paymentInfo?.merchantReferenceId}${paymentInfo?.customerMerchantProfileId}${paymentInfo?.amount}$hashKey"
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}