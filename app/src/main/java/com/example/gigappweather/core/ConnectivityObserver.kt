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
        // Prefer being tolerant here: some emulators/devices can have multiple networks where
        // `activeNetwork` is not the one that actually provides internet.
        return cm.allNetworks
            .asSequence()
            .mapNotNull { network -> cm.getNetworkCapabilities(network) }
            .any { caps ->
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            }
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

        // Listen for any network that could provide internet.
        cm.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            callback,
        )
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
