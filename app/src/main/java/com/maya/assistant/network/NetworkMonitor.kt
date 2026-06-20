package com.maya.assistant.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.নেটওয়ার্ক
import android.net.নেটওয়ার্কCapabilities
import android.net.নেটওয়ার্কRequest
import com.maya.assistant.utils.Logger

class নেটওয়ার্কMonitor(context: Context) {
    private val TAG = "NET"
    private val cm = context.getসিস্টেমService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isসংযুক্ত ✅ = false
        private set
    var onConnectionChange: ((Boolean) -> Unit)? = null

    private val callback = object : ConnectivityManager.নেটওয়ার্ককলback() {
        override fun onপাওয়া যাচ্ছে(network: নেটওয়ার্ক) {
            isসংযুক্ত ✅ = true
            Logger.d(TAG, "নেটওয়ার্ক available")
            onConnectionChange?.invoke(true)
        }
        override fun onLost(network: নেটওয়ার্ক) {
            isসংযুক্ত ✅ = false
            Logger.d(TAG, "নেটওয়ার্ক lost")
            onConnectionChange?.invoke(false)
        }
    }

    fun start() {
        val req = নেটওয়ার্কRequest.Builder()
            .addCapability(নেটওয়ার্কCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerনেটওয়ার্ককলback(req, callback)
        isসংযুক্ত ✅ = cm.activeনেটওয়ার্ক != null
    }

    fun stop() {
        try { cm.unregisterনেটওয়ার্ককলback(callback) } catch (_: Exception) {}
    }
}
