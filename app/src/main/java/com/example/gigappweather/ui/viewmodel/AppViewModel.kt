package com.example.gigappweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigappweather.core.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val _simulatedOffline = MutableStateFlow(false)
    val simulatedOffline: StateFlow<Boolean> = _simulatedOffline.asStateFlow()

    // Demo behavior:
    // - simulatedOffline = true  => force Offline UI mode
    // - simulatedOffline = false => follow real connectivity
    val isOnline: StateFlow<Boolean> = connectivityObserver
        .isOnlineFlow()
        .combine(_simulatedOffline) { realOnline, simOffline ->
            realOnline && !simOffline
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setSimulatedOffline(value: Boolean) {
        _simulatedOffline.value = value
    }

    fun toggleSimulatedOffline() {
        _simulatedOffline.value = !_simulatedOffline.value
    }
}
