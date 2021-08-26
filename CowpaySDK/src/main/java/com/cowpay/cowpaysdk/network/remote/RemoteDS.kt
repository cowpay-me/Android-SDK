package com.cowpay.cowpaysdk.network.remote

import com.cowpay.cowpaysdk.models.CowpayRequest
import com.cowpay.cowpaysdk.models.CowpayResponse
import com.cowpay.cowpaysdk.network.base.ApiService
import com.cowpay.cowpaysdk.network.base.BaseRemoteDataSource
import com.cowpay.cowpaysdk.network.base.BaseResponse
import com.cowpay.cowpaysdk.utils.ui

class RemoteDS(private val apiService: ApiService): BaseRemoteDataSource() {


    fun payWithFawry(request : CowpayRequest,
                     callbacks: BaseResponse<CowpayResponse>
    ) {
        ui {
            apiRequest({
                apiService.payWithFawry(request)
            }, callbacks)
        }
    }

    fun payWithCard(request : CowpayRequest,
                     callbacks: BaseResponse<CowpayResponse>
    ) {
        ui {
            apiRequest({
                apiService.payWithCard(request)
            }, callbacks)
        }
    }

}