package com.cowpay.cowpaysdk.sdk

import com.cowpay.cowpaysdk.models.Card
import com.cowpay.cowpaysdk.models.CashCollection
import com.cowpay.cowpaysdk.models.Fawry

interface CowpayCallback {
    fun successByFawry(fawry: Fawry)
    fun successByCreditCard(card: Card)
    fun successByCashCollection(cashCollection: CashCollection)
    fun error(it: String)
    fun closeByUser()
}