package com.cowpay.cowpaysdk.utils

import android.app.Activity
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import com.cowpay.cowpaysdk.ui.dialogs.AppDialogFragment
import com.cowpay.cowpaysdk.ui.dialogs.DialogCallback
import com.thecode.aestheticdialogs.*

object DialogHelper {

    fun showError(context: FragmentManager, @DrawableRes drawableRes:Int, str: String, callback: DialogCallback) {

        val dialog = AppDialogFragment.newInstance(drawableRes,null,str,"Ok",null)
        dialog.positiveCallback = callback
        dialog.show(context,"dialog")
    }

    fun showSuccess(context: Activity, str: String, callback: OnDialogClickListener) {

        AestheticDialog.Builder(context, DialogStyle.FLAT, DialogType.SUCCESS)
            .setTitle("Success")
            .setMessage(str)
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(callback)
            .show()
    }
}