package com.cowpay.cowpaysdk.sdk

import com.cowpay.cowpaysdk.models.Card
import com.cowpay.cowpaysdk.models.Fawry

interface CowpayCallback {
    fun successByFawry(fawry: Fawry)
    fun successByCreditCard(card: Card)
    fun error(it: String)
    fun closeByUser()
}