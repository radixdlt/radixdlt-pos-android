package com.radixdlt.android.apps.pos.ui.payment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper

abstract class ConnectionStateMonitor : ConnectivityManager.NetworkCallback() {

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    fun enable(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun disable(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        Handler(Looper.getMainLooper()).post {
            isConnected(true)
        }
    }

    override fun onLost(network: Network?) {
        super.onLost(network)
        Handler(Looper.getMainLooper()).post {
            isConnected(false)
        }
    }

    abstract fun isConnected(connected: Boolean)
}
