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
    observer: ConnectivityObserver,
) : ViewModel() {

    private val _simulatedOffline = MutableStateFlow(false)
    val simulatedOffline: StateFlow<Boolean> = _simulatedOffline.asStateFlow()

    val realOnline: StateFlow<Boolean> = observer.isOnlineFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    // Demo behavior:
    // - simulatedOffline = true  => force Offline
    // - simulatedOffline = false => use real connectivity
    val isOnline: StateFlow<Boolean> = realOnline
        .combine(_simulatedOffline) { online, simOffline -> online && !simOffline }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setSimulatedOffline(value: Boolean) {
        _simulatedOffline.value = value
    }

    fun toggleSimulatedOffline() {
        _simulatedOffline.value = !_simulatedOffline.value
    }
}
