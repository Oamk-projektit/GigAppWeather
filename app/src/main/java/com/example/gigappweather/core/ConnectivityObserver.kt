package com.example.gigappweather.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityObserver(context: Context) {
    private val appContext = context.applicationContext
    private val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun isOnlineNow(): Boolean {
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        // NOTE: Some emulators/devices don't reliably report VALIDATED even when browsing works.
        // For the purposes of a visible online/offline indicator, treat INTERNET capability as online.
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isOnlineFlow(): Flow<Boolean> = callbackFlow {
        fun emitNow() {
            trySend(isOnlineNow())
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = emitNow()
            override fun onLost(network: Network) = emitNow()
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) = emitNow()
        }

        emitNow()

        cm.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
