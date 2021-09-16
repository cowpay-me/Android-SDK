package com.cowpay.cowpaysdk.ui.activity.credit_card

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cowpay.cowpaysdk.R
import com.cowpay.cowpaysdk.models.Card
import com.cowpay.cowpaysdk.models.CowpayResponse
import com.cowpay.cowpaysdk.sdk.CowpaySDK
import com.cowpay.cowpaysdk.utils.DialogHelper
import com.cowpay.cowpaysdk.utils.MonthYearPickerDialog
import com.cowpay.cowpaysdk.utils.PaymentCardType
import com.google.gson.Gson
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.OnDialogClickListener
import kotlinx.android.synthetic.main.activity_credit_card.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class CreditCardActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var viewModel: CreditCardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        viewModel = ViewModelProvider(this).get(CreditCardViewModel::class.java)
        observe()
        listener()
        tvTitle.text = "Pay Now"
    }


    private fun listener() {
        etCardNumber.addTextChangedListener {
            viewModel.cardNumberChanged(etCardNumber.text.toString())
        }

        btnAdd.setOnClickListener {
            viewModel.addCard(
                etCardName.text.toString(),
                etCardNumber.text.toString(),
                tvExpiryDate.text.toString(),
                tvCardCVV.text.toString()
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


    private fun observe() {
        viewModel.cardTypeSelected.observe(this, Observer {

            clearCardIcons()
            val selectedImg = when (it) {
                PaymentCardType.VISA -> ivVisa
                PaymentCardType.MASTERCARD -> ivMasterCard
                else -> null
            }
            selectedImg?.alpha = 1f
        })

        viewModel.bankCardValidationMsg.observe(this, Observer {
//            DialogHelper.showError(this, getString(it), object : OnDialogClickListener {
//                override fun onClick(dialog: AestheticDialog.Builder) {
//                    dialog.dismiss()
//                }
//            })
        })

        viewModel.loading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
            btnAdd.visibility = if (it) View.INVISIBLE else View.VISIBLE
        })

        viewModel.lunchWebview.observe(this, Observer {
            loadWebviewForPayment(it)
        })
        viewModel.error.observe(this, Observer {
//            DialogHelper.showError(this,it, object : OnDialogClickListener {
//                override fun onClick(dialog: AestheticDialog.Builder) {
//                    dialog.dismiss()
//                }
//            })
        })
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearCardIcons() {
        ivVisa.alpha = 0.5f
        ivMasterCard.alpha = 0.5f
        ivMeezaCard.alpha = 0.5f
    }

    fun loadWebviewForPayment(token: String){
        llInfo.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("${CowpaySDK.getUrlWebView()}${token}")
        webView.addJavascriptInterface(JSBridge(this), "JSBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, weburl: String) {
                if(weburl.contains("transaction_id")){
                    webView.visibility = View.GONE
                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CowpaySDK.callback?.closeByUser()
    }

    class JSBridge(val activity: Activity){
        @JavascriptInterface
        fun showMessageInNative(message: String){
            val obj = Gson().fromJson(message, CowpayResponse::class.java)
            if(obj.paymentStatus == "PAID"){
                DialogHelper.showSuccess(
                    activity,
                    "Payment completed successfully",
                    object : OnDialogClickListener {
                        override fun onClick(dialog: AestheticDialog.Builder) {
                            dialog.dismiss()
                            activity.finish()
                            CowpaySDK.callback?.successByCreditCard(
                                Card(
                                    obj.paymentGatewayReferenceId,
                                    obj.cowpayReferenceId
                                )
                            )

                        }
                    })
            }else {
                val msg = "Payment failed"
//                DialogHelper.showError(activity, msg, object : OnDialogClickListener {
//                    override fun onClick(dialog: AestheticDialog.Builder) {
//                        dialog.dismiss()
//                        activity.finish()
//                        CowpaySDK.callback?.error(msg)
//                    }
//                })
            }
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val month = if(p2<10) "0${p2}" else p2.toString()
        tvExpiryDate.setText("$month${viewModel.splitStr()}${p1.toString().drop(2)}")
    }

}