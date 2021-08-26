package com.cowpay.cowpay

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cowpay.cowpaysdk.models.Card
import com.cowpay.cowpaysdk.models.Fawry
import com.cowpay.cowpaysdk.sdk.CowpayCallback
import com.cowpay.cowpaysdk.sdk.CowpayEnviroment
import com.cowpay.cowpaysdk.sdk.CowpaySDK
import com.cowpay.cowpaysdk.sdk.CowpayScreenMode
import com.cowpay.cowpaysdk.sdk.model.PaymentInfo
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}
