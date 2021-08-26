package com.cowpay.cowpaysdk.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


 fun ui(invoke: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    invoke.invoke()
}

fun network(invoke: suspend () -> Unit) = CoroutineScope(Dispatchers.IO).launch {
    invoke.invoke()
}

fun delayExt(time:Long,invoke: suspend () -> Unit) = ui {
    delay(time)
    invoke.invoke()
}