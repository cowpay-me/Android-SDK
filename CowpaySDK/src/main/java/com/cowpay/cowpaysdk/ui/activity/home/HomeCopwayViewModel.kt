package com.cowpay.cowpaysdk.ui.activity.home

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cowpay.cowpaysdk.R
import com.cowpay.cowpaysdk.models.CardRequest
import com.cowpay.cowpaysdk.models.CashCollection
import com.cowpay.cowpaysdk.models.CashCollectionRequest
import com.cowpay.cowpaysdk.models.Fawry
import com.cowpay.cowpaysdk.network.base.BaseResponse
import com.cowpay.cowpaysdk.sdk.CowpayOperator
import com.cowpay.cowpaysdk.utils.*

class HomeCopwayViewModel  : ViewModel(){

    val bankCardValidationMsg = MutableLiveData<@StringRes Int>()
    val cardTypeSelected = MutableLiveData<PaymentCardType>()
    val invalidInputCashCollection = MutableLiveData<CashCollectionValidationType>()
    val loading = MutableLiveData<Boolean>()
    val lunchWebview = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val fawry = MutableLiveData<Fawry>()
    val cashCollection = MutableLiveData<CashCollection>()

    var paymentType = PaymentType.CARD

    fun selectCardOption() {
        paymentType = PaymentType.CARD
    }

    fun selectFawry() {
        paymentType = PaymentType.FAWRY
    }

    fun selectCashCollection() {
        paymentType = PaymentType.CASH_COLLECTION
    }


    fun cardNumberChanged(number: String) {
        cardTypeSelected.value = PaymentHelper.getPaymentCardType(number.replace(" ", ""))
    }

    private fun payWithFawry() {
        loading.value = true
        CowpayOperator().payWithFawry(
            BaseResponse(
                onResult = {
                    loading.postValue(false)
                    fawry.postValue(
                        Fawry(
                            it?.paymentGatewayReferenceId, it?.merchantReferenceId,
                            it?.cowpayReferenceId
                        )
                    )
                }, onError = {
                    loading.postValue(false)
                    error.postValue(it)
                }
            ))
    }

    fun pay(
        cardRequest: CardRequest? = null,
        cashCollectionRequest: CashCollectionRequest? = null
    ) {
        if (paymentType == PaymentType.FAWRY) {
            payWithFawry()
        } else if (paymentType == PaymentType.CARD && cardRequest != null) {
            payWithCard(cardRequest.name, cardRequest.number, cardRequest.date, cardRequest.cvv)
        } else if (paymentType == PaymentType.CASH_COLLECTION && cashCollectionRequest != null) {
            payWithCashCollection(cashCollectionRequest)
        }
    }

    private fun payWithCard(name: String, number: String, date: String, cvv: String) {
        val cardNumber = number.replace(" ", "")
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
                saveCard(name, cardNumber, date, cvv)
            }
        }
    }

    private fun payWithCashCollection(cashCollectionRequest: CashCollectionRequest) {
        when {
            cashCollectionRequest.customerName.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_NAME
            }
            cashCollectionRequest.customerPhone.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_PHONE
            }
            cashCollectionRequest.customerEmail.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_EMAIL
            }
            cashCollectionRequest.address.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_ADDRESS
            }
            cashCollectionRequest.floor.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_FLOOR
            }
            cashCollectionRequest.district.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_DISTRICT
            }
            cashCollectionRequest.apartment.isEmpty() -> {
                invalidInputCashCollection.value = CashCollectionValidationType.INVALID_APARTMENT
            }
            else -> {
                invalidInputCashCollection.value = CashCollectionValidationType.CLEAR
                sendCashCollection(cashCollectionRequest)
            }
        }
    }

    private fun sendCashCollection(cashCollectionRequest: CashCollectionRequest) {
        loading.postValue(true)
        CowpayOperator().payWithCahCollection(
            cashCollectionRequest, getCityCodeFromIndex(cashCollectionRequest.cityIndex),
            BaseResponse({
                loading.postValue(false)
                cashCollection.postValue(
                    CashCollection(
                        it?.paymentGatewayReferenceId, it?.merchantReferenceId,
                        it?.cowpayReferenceId
                    )
                )
            }, {
                loading.postValue(false)
                error.postValue(it)
            })
        )
    }

    private fun getCityCodeFromIndex(cityIndex: Int): String {
        return cityCode[cityIndex] ?: "EG-01"
    }

    private var cityCode = hashMapOf(
        0 to "EG-01",
        1 to "EG-01",
        2 to "EG-02",
        3 to "EG-03",
        4 to "EG-04",
        5 to "EG-05",
        6 to "EG-06",
        7 to "EG-07",
        8 to "EG-08",
        9 to "EG-09",
        10 to "EG-10",
        11 to "EG-11",
        12 to "EG-12",
        13 to "EG-13",
        14 to "EG-14",
        15 to "EG-15",
        16 to "EG-16",
        17 to "EG-17",
        18 to "EG-19",
        19 to "EG-20",
        20 to "EG-21",
        21 to "EG-22",


        )

    private fun saveCard(name: String, number: String, date: String, cvv: String) {
        loading.postValue(true)
        val arr = date.split(splitStr())
        CowpayOperator().payWithCreditCard(
            name,
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