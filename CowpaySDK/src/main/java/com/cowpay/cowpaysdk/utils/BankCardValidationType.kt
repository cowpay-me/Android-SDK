package com.cowpay.cowpaysdk.utils

enum class BankCardValidationType {
    INVALID_NAME, INVALID_NUMBER, INVALID_DATE, INVALID_CVV, VALID
}

enum class CashCollectionValidationType {
    INVALID_NAME, INVALID_EMAIL, INVALID_PHONE, INVALID_ADDRESS,INVALID_FLOOR,INVALID_DISTRICT,INVALID_APARTMENT, CLEAR
}

enum class PaymentCardType{
    VISA,MASTERCARD,NON
}