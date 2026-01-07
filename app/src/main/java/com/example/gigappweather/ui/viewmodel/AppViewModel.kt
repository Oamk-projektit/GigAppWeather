package com.example.gigappweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
) : ViewModel() {

    private val _simulatedOffline = MutableStateFlow(false)
    val simulatedOffline: StateFlow<Boolean> = _simulatedOffline.asStateFlow()

    // Demo behavior:
    // - simulatedOffline = true  => force Offline
    // - simulatedOffline = false => force Online
    val isOnline: StateFlow<Boolean> = _simulatedOffline
        .map { simOffline -> !simOffline }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setSimulatedOffline(value: Boolean) {
        _simulatedOffline.value = value
    }

    fun toggleSimulatedOffline() {
        _simulatedOffline.value = !_simulatedOffline.value
    }
}
