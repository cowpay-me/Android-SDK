package com.cowpay.cowpaysdk.utils

object PaymentHelper {

    private const val visaRegExFull = "^4[0-9]{6,}\$" //"^(?:4[0-9]{12}(?:[0-9]{3})?)$"
    private const val mastercardRegExFull = "^5[1-5][0-9]{5,}\$" // "^(?:5[1-5][0-9]{14})$"
    private const val visaRegExMasked = "^(?:4[0-9]{5}[*]{6}[0-9]{4}?)$"
    private const val mastercardRegExMasked = "^(?:5[1-5][0-9]{4}[*]{6}[0-9]{4})$"


    private val meezaBin = "507803"

    fun getPaymentCardType(number: String): PaymentCardType {

        val num = number.subSequence(
            0,
            if (number.length >= 6) 6 else number.length
        )
        return when {
            number.matches(regex = visaRegExMasked.toRegex()) -> PaymentCardType.VISA
            number.matches(regex = visaRegExFull.toRegex()) -> PaymentCardType.VISA
            number.matches(regex = mastercardRegExFull.toRegex()) -> PaymentCardType.MASTERCARD
            number.matches(regex = mastercardRegExMasked.toRegex()) -> PaymentCardType.MASTERCARD

            else -> PaymentCardType.NON
        }
    }
}