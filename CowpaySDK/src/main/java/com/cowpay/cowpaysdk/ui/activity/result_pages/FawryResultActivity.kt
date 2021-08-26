package com.cowpay.cowpaysdk.ui.activity.result_pages

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cowpay.cowpaysdk.R
import com.cowpay.cowpaysdk.models.Fawry
import com.cowpay.cowpaysdk.sdk.CowpaySDK
import kotlinx.android.synthetic.main.activity_fawry_result.*


class FawryResultActivity : AppCompatActivity() {

    var fawry: Fawry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fawry_result)
        setData()
        listener()
    }

    private fun setData() {
        fawry = intent.getParcelableExtra("fawry")
        fawry?.let {
            tvCode.text = it.paymentGatewayReferenceId
            tvCodeSmall.append(it.paymentGatewayReferenceId)
        }?: kotlin.run {
            finish()
        }

    }


    private fun listener() {
        tvBack.setOnClickListener {
            onBackPressed()
        }

        tvCopyCode.setOnClickListener {
            copyCode()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fawry?.let {
            CowpaySDK.callback?.successByFawry(it)
            finish()
        }
    }

    private fun copyCode(){

        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Code", tvCode.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,"Code Copied",Toast.LENGTH_LONG).show()
    }
}