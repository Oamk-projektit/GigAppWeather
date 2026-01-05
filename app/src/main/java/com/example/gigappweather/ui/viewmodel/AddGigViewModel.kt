package com.example.gigappweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigappweather.R
import com.example.gigappweather.core.UiState
import com.example.gigappweather.domain.model.Gig
import com.example.gigappweather.domain.repository.GigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddGigViewModel(
    private val gigRepository: GigRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Long>>(UiState.Empty)
    val state: StateFlow<UiState<Long>> = _state.asStateFlow()

    fun addGig(
        title: String,
        dateIso: String,
        city: String,
        isOutdoor: Boolean,
        createdAt: Long,
    ) {
        viewModelScope.launch(ioDispatcher) {
            _state.value = UiState.Loading
            try {
                val id = gigRepository.insert(
                    Gig(
                        title = title,
                        dateIso = dateIso,
                        city = city,
                        isOutdoor = isOutdoor,
                        createdAt = createdAt,
                    )
                )
                _state.value = UiState.Success(id)
            } catch (_: Exception) {
                _state.value = UiState.Error(messageRes = R.string.error_db)
            }
        }
    }
}
