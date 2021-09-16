package com.cowpay.cowpaysdk.ui.activity.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cowpay.cowpaysdk.R
import com.cowpay.cowpaysdk.models.Card
import com.cowpay.cowpaysdk.models.CardRequest
import com.cowpay.cowpaysdk.models.CashCollectionRequest
import com.cowpay.cowpaysdk.models.CowpayResponse
import com.cowpay.cowpaysdk.sdk.CowpaySDK
import com.cowpay.cowpaysdk.sdk.CowpaySDKPaymentMethod
import com.cowpay.cowpaysdk.ui.activity.result_pages.FawryResultActivity
import com.cowpay.cowpaysdk.utils.CashCollectionValidationType
import com.cowpay.cowpaysdk.utils.DialogHelper
import com.cowpay.cowpaysdk.utils.MonthYearPickerDialog
import com.cowpay.cowpaysdk.utils.PaymentCardType
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_cash_collection.*
import kotlinx.android.synthetic.main.layout_credit_card.*
import kotlinx.android.synthetic.main.toolbar.*

internal class HomeCopwayActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: HomeCopwayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
        viewModel = ViewModelProvider(this).get(HomeCopwayViewModel::class.java)
        listener()
        observer()
        setAvailableOptions()
    }

    private fun setCashCollectionData() {
        etCustomerName.setText(CowpaySDK.paymentInfo?.customerName ?: "")
        etCustomerEmail.setText(CowpaySDK.paymentInfo?.customerEmail ?: "")
        etCustomerMobile.setText(CowpaySDK.paymentInfo?.customerMobile ?: "")
    }

    var visa: Drawable? = null
    var master: Drawable? = null
    var fawryText: SpannableStringBuilder? = null

    fun init() {
        visa = ContextCompat.getDrawable(this, R.drawable.ic_visa_dark_copy)
        master = ContextCompat.getDrawable(this, R.drawable.ic_mastercard_color)
        fawryText = SpannableStringBuilder()
            .append(getString(R.string.please_click))
            .color(Color.BLACK) { bold { append(getString(R.string.confirm_payment)) } }
            .append(getString(R.string.btn_to_generate))
            .color(Color.BLACK) { bold { append(getString(R.string.ref_num)) } }
        layoutFawry.text = fawryText
        setCashCollectionData()
    }

    private fun setAvailableOptions() {
        if (CowpaySDK.paymentMethodAvailability.contains(CowpaySDKPaymentMethod.CASH_COLLECTION)) {
            flCashCollection.visibility = View.VISIBLE
            flCashCollection.performClick()
        }
        if (CowpaySDK.paymentMethodAvailability.contains(CowpaySDKPaymentMethod.FAWRY)) {
            flFawry.visibility = View.VISIBLE
            flFawry.performClick()
        }
        if (CowpaySDK.paymentMethodAvailability.contains(CowpaySDKPaymentMethod.CARD)) {
            flCreditCard.visibility = View.VISIBLE
            flCreditCard.performClick()
        }
    }

    private fun observer() {
        viewModel.cardTypeSelected.observe(this, {

            val selectedImg = when (it) {
                PaymentCardType.VISA -> visa
                PaymentCardType.MASTERCARD -> master
                else -> null
            }
            etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, selectedImg, null)
        })

        viewModel.bankCardValidationMsg.observe(this, Observer {
            DialogHelper.showError(supportFragmentManager, R.drawable.error, getString(it)) {}
        })
        viewModel.loading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
            btnPay.visibility = if (it) View.INVISIBLE else View.VISIBLE
        })

        viewModel.lunchWebview.observe(this, Observer {
            loadWebviewForPayment(it)
        })
        viewModel.error.observe(this, Observer {
            DialogHelper.showError(this.supportFragmentManager, R.drawable.error, it) { }
        })

        viewModel.fawry.observe(this, Observer {
            val intent = Intent(this, FawryResultActivity::class.java)
            intent.putExtra("fawry",it)
            startActivity(intent)
            finish()
        })

        viewModel.cashCollection.observe(this, Observer {
            DialogHelper.showError(
                supportFragmentManager,
                R.drawable.success,
                title = it.paymentGatewayReferenceId,
                str = CowpaySDK.successMsgForCashCollection
                    ?: getString(R.string.succes_msg_for_cash_collection)
            ) {
                CowpaySDK.callback?.successByCashCollection(it)
            }
        })

        viewModel.invalidInputCashCollection.observe(this, Observer {
            when(it){
                CashCollectionValidationType.INVALID_NAME -> etCustomerName.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_EMAIL -> etCustomerEmail.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_PHONE -> etCustomerMobile.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_ADDRESS -> etAddress.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_FLOOR -> etFloor.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_DISTRICT -> etDistrict.error = getString(R.string.required)
                CashCollectionValidationType.INVALID_APARTMENT -> etApartment.error = getString(R.string.required)
            }
        })
    }

    fun loadWebviewForPayment(token: String){
        layoutCreditCard.visibility = View.GONE
        svTabs.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("${CowpaySDK.getUrlWebView()}${token}")
        webView.addJavascriptInterface(JSBridge(this, supportFragmentManager), "JSBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, weburl: String) {
                if(weburl.contains("transaction_id")){
                    webView.visibility = View.GONE
                }

            }
        }
    }
    private fun listener() {
        ivBack.setOnClickListener {
            finish()
            CowpaySDK.callback?.closeByUser()
        }

        flCreditCard.setOnClickListener {
            viewModel.selectCardOption()
            clearSelection()
            selectItem(it)
            tvCard.setTextColor(ContextCompat.getColor(this,R.color.primaryColor2))
            tvCard.setCompoundDrawablesWithIntrinsicBounds(null,ContextCompat.getDrawable(this,R.drawable.ic_credit_card_primary),null,null)
            layoutCreditCard.visibility = View.VISIBLE
            layoutFawry.visibility = View.GONE
            layoutCashCollection.visibility = View.GONE
        }

        flFawry.setOnClickListener {
            viewModel.selectFawry()
            clearSelection()
            selectItem(it)
            tvFawry.setTextColor(ContextCompat.getColor(this, R.color.primaryColor2))
            tvFawry.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ContextCompat.getDrawable(this, R.drawable.icon_fawry_primary),
                null,
                null
            )
            layoutCreditCard.visibility = View.GONE
            layoutCashCollection.visibility = View.GONE
            layoutFawry.visibility = View.VISIBLE
        }

        flCashCollection.setOnClickListener {
            viewModel.selectCashCollection()
            clearSelection()
            selectItem(it)
            tvCashCollection.setTextColor(ContextCompat.getColor(this, R.color.primaryColor2))
            tvCashCollection.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ContextCompat.getDrawable(this, R.drawable.ic_cash_collection_primary),
                null,
                null
            )
            layoutCreditCard.visibility = View.GONE
            layoutFawry.visibility = View.GONE
            layoutCashCollection.visibility = View.VISIBLE
        }

        etCardNumber.addTextChangedListener {
            viewModel.cardNumberChanged(etCardNumber.text.toString())
        }

        btnPay.setOnClickListener {
            viewModel.pay(
                CardRequest(
                    etCardName.text.toString(),
                    etCardNumber.text.toString(),
                    tvExpiryDate.text.toString(),
                    tvCardCVV.text.toString()
                ),
                CashCollectionRequest(
                    etCustomerName.text.toString(),
                    etCustomerMobile.text.toString(),
                    etCustomerEmail.text.toString(),
                    etAddress.text.toString(),
                    etFloor.text.toString(),
                    etDistrict.text.toString(),
                    etApartment.text.toString(),
                    spCities.selectedItemPosition
                )
            )
        }

        tvExpiryDate.setOnClickListener {
            val pickerDialog = MonthYearPickerDialog()
            pickerDialog.setListener(this)
            pickerDialog.show(supportFragmentManager, "MonthYearPickerDialog")
        }

        etCardNumber.addTextChangedListener { s ->
            s?.let { addDashToEditText(s) }
        }
    }



    private val space = ' '
    private fun addDashToEditText(s: Editable) {
        // Remove spacing char
        if (s.isNotEmpty() && s.length % 5 == 0) {
            val c = s[s.length - 1]
            if (space == c) {
                s.delete(s.length - 1, s.length)
            }
        }
        // Insert char where needed.
        if (s.isNotEmpty() && s.length % 5 == 0) {
            val c = s[s.length - 1]
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(
                    s.toString(),
                    space.toString()
                ).size <= 3
            ) {
                s.insert(s.length - 1, space.toString())
            }
        }
    }

    override fun onBackPressed() {
        finish()
        CowpaySDK.callback?.closeByUser()

    }

    private fun clearSelection(){
        flCreditCard.background = ContextCompat.getDrawable(this,R.drawable.raduis_10_grey)
        flFawry.background = ContextCompat.getDrawable(this, R.drawable.raduis_10_grey)
        flCashCollection.background = ContextCompat.getDrawable(this, R.drawable.raduis_10_grey)
        tvCard.setTextColor(ContextCompat.getColor(this,R.color.black))
        tvFawry.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvCashCollection.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvCard.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(this, R.drawable.ic_credit_card),
            null,
            null
        )
        tvFawry.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(this, R.drawable.icon_fawry),
            null,
            null
        )
        tvCashCollection.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(this, R.drawable.ic_cash_collection),
            null,
            null
        )
    }

    private fun selectItem(fl: View){
        fl.background = ContextCompat.getDrawable(this,R.drawable.raduis_10)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val month = if(p2<10) "0${p2}" else p2.toString()
        tvExpiryDate.setText("$month${viewModel.splitStr()}${p1.toString().drop(2)}")
    }

    class JSBridge(val activity: Activity, val fragmentManager: FragmentManager) {
        @JavascriptInterface
        fun showMessageInNative(message: String) {
            val obj = Gson().fromJson(message, CowpayResponse::class.java)
            if (obj.paymentStatus == "PAID") {
                DialogHelper.showError(
                    fragmentManager,
                    R.drawable.success,
                    activity.getString(R.string.succes_msg_for_card)
                )
                {
                    activity.finish()
                    CowpaySDK.callback?.successByCreditCard(
                        Card(
                            obj.paymentGatewayReferenceId,
                            obj.cowpayReferenceId
                        )
                    )
                }
            }else {
                val msg = activity.getString(R.string.error_msg_for_card)
                DialogHelper.showError(fragmentManager, R.drawable.error, msg) {
                    activity.finish()
                    CowpaySDK.callback?.error(msg)
                }
            }
        }
    }

}