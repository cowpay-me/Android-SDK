package com.cowpay.cowpaysdk.sdk

import com.cowpay.cowpay.network.base.AuthInterceptor
import com.cowpay.cowpay.network.base.RetroClient
import com.cowpay.cowpaysdk.models.CashCollectionRequest
import com.cowpay.cowpaysdk.models.CowpayRequest
import com.cowpay.cowpaysdk.models.CowpayResponse
import com.cowpay.cowpaysdk.network.base.ApiService
import com.cowpay.cowpaysdk.network.base.BaseResponse
import com.cowpay.cowpaysdk.network.remote.RemoteDS

internal class CowpayOperator {

    fun payWithFawry(callbacks: BaseResponse<CowpayResponse>){
        val request = CowpayRequest()
        request.merchantReferenceId = CowpaySDK.paymentInfo?.merchantReferenceId
        request.customerMerchantProfileId = CowpaySDK.paymentInfo?.customerMerchantProfileId
        request.amount = CowpaySDK.paymentInfo?.amount.toString()
        request.customerName = CowpaySDK.paymentInfo?.customerName
        request.customerMobile = CowpaySDK.paymentInfo?.customerMobile
        request.customerEmail = CowpaySDK.paymentInfo?.customerEmail
        request.description = CowpaySDK.paymentInfo?.description
        request.signature = CowpaySDK.signature
        getRemoteDS()?.payWithFawry(request,callbacks = callbacks)
    }

    fun payWithCreditCard(
        cardName:String,
        cardNumber:String,
        cardYear:String,
        cardMonth:String,
        cardCvv:String,
        callbacks: BaseResponse<CowpayResponse>){
        val request = CowpayRequest()
        request.merchantReferenceId = CowpaySDK.paymentInfo?.merchantReferenceId
        request.customerMerchantProfileId = CowpaySDK.paymentInfo?.customerMerchantProfileId
        request.amount = CowpaySDK.paymentInfo?.amount.toString()
        request.customerName = CowpaySDK.paymentInfo?.customerName
        request.customerMobile = CowpaySDK.paymentInfo?.customerMobile
        request.customerEmail = CowpaySDK.paymentInfo?.customerEmail
        request.description = CowpaySDK.paymentInfo?.description
        request.signature = CowpaySDK.signature
        request.cardNumber = cardNumber
        request.expiryYear = cardYear
        request.expiryMonth = cardMonth
        request.cardHolder = cardName
        request.cvv = cardCvv
        getRemoteDS()?.payWithCard(request,callbacks = callbacks)
    }

    fun payWithCahCollection(
        cashCollectionRequest: CashCollectionRequest,
        cityCode:String,
        callbacks: BaseResponse<CowpayResponse>){
        val request = CowpayRequest()
        request.merchantReferenceId = CowpaySDK.paymentInfo?.merchantReferenceId
        request.customerMerchantProfileId = CowpaySDK.paymentInfo?.customerMerchantProfileId
        request.amount = CowpaySDK.paymentInfo?.amount.toString()
        request.customerName = cashCollectionRequest.customerName
        request.customerMobile = cashCollectionRequest.customerPhone
        request.customerEmail = cashCollectionRequest.customerEmail
        request.description = CowpaySDK.paymentInfo?.description
        request.signature = CowpaySDK.signature
        request.address = cashCollectionRequest.address
        request.floor = cashCollectionRequest.floor
        request.district = cashCollectionRequest.district
        request.apartment = cashCollectionRequest.apartment
        request.cityCode = cityCode
        getRemoteDS()?.payWithCahCollection(request,callbacks = callbacks)
    }

    private var rDS:RemoteDS? = null

    private fun getRemoteDS() : RemoteDS? {
        if(rDS == null)
            rDS = RemoteDS(
            ApiService.createAuthService(
                RetroClient.provideRetrofit(
                    RetroClient.provideOkHttpClient(
                        AuthInterceptor()
                    )
                )
            )
        )
        return rDS
    }
}