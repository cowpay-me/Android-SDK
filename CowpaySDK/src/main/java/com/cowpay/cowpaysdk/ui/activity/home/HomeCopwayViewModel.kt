package com.cowpay.cowpaysdk.ui.activity.home

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cowpay.cowpaysdk.R
import com.cowpay.cowpaysdk.models.Fawry
import com.cowpay.cowpaysdk.network.base.BaseResponse
import com.cowpay.cowpaysdk.sdk.CowpayOperator
import com.cowpay.cowpaysdk.utils.BankCardValidationType
import com.cowpay.cowpaysdk.utils.PaymentCardType
import com.cowpay.cowpaysdk.utils.PaymentHelper
import com.cowpay.cowpaysdk.utils.PaymentType

class HomeCopwayViewModel  : ViewModel(){

    val bankCardValidationMsg = MutableLiveData<@StringRes Int>()
    val cardTypeSelected = MutableLiveData<PaymentCardType>()
    val loading = MutableLiveData<Boolean>()
    val lunchWebview = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val fawry = MutableLiveData<Fawry>()

    var paymentType = PaymentType.CARD

    fun selectCardOption(){
        paymentType = PaymentType.CARD
    }

    fun selectFawry(){
        paymentType = PaymentType.FAWRY
    }


    fun cardNumberChanged(number: String) {
        cardTypeSelected.value = PaymentHelper.getPaymentCardType(number.replace(" ",""))
    }

    private fun payWithFawry(){
        loading.value = true
        CowpayOperator().payWithFawry(BaseResponse(
            onResult = {
                loading.postValue(false)
                fawry.postValue(
                    Fawry(it?.paymentGatewayReferenceId,it?.merchantReferenceId,
                    it?.cowpayReferenceId
                )
                )
            },onError = {
                loading.postValue(false)
                error.postValue(it)
            }
        ))
    }

    fun pay(name: String, number: String, date: String, cvv: String){
        if(paymentType == PaymentType.FAWRY){
            payWithFawry()
        }else if(paymentType == PaymentType.CARD){
            payWithCard(name, number, date, cvv)
        }
    }
    private fun payWithCard(name: String, number: String, date: String, cvv: String) {
        val cardNumber = number.replace(" ","")
        when (validCard(name, cardNumber , date, cvv)) {
            BankCardValidationType.INVALID_NAME -> {
                bankCardValidationMsg.value = R.string.invalid_card_name
            }
            BankCardValidationType.INVALID_NUMBER -> {
                bankCardValidationMsg.value = R.string.invalid_card_number
            }
            BankCardValidationType.INVALID_DATE -> {
                bankCardValidationMsg.value = R.string.invalid_card_date
            }
            BankCardValidationType.INVALID_CVV -> {
                bankCardValidationMsg.value = R.string.invalid_card_cvv
            }
            BankCardValidationType.VALID -> {
                saveCard(name,cardNumber, date, cvv)
            }
        }
    }

    private fun saveCard(name:String,number: String, date: String, cvv: String) {
        loading.postValue(true)
        val arr = date.split(splitStr())
        CowpayOperator().payWithCreditCard(name,
            number, arr[1], arr[0], cvv, BaseResponse({
                loading.postValue(false)
                it?.token?.let { lunchWebview.postValue(it) }
            }, {
                loading.postValue(false)
                error.postValue(it)
            })
        )
    }

    fun splitStr(): String {
        return "/"
    }

    private fun validCard(
        name: String,
        number: String,
        date: String,
        cvv: String
    ): BankCardValidationType {
        return when {
            name.isEmpty() -> {
                BankCardValidationType.INVALID_NAME
            }
            number.length != 16 -> {
                BankCardValidationType.INVALID_NUMBER
            }
            date.isEmpty() -> {
                BankCardValidationType.INVALID_DATE
            }
            cvv.length != 3 -> {
                BankCardValidationType.INVALID_CVV
            }
            else -> BankCardValidationType.VALID
        }

    }
}