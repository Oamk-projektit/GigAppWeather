package com.example.gigappweather.ui.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gigappweather.core.ConnectivityObserver
import com.example.gigappweather.domain.repository.GigRepository
import com.example.gigappweather.domain.repository.WeatherRepository
import com.example.gigappweather.ui.viewmodel.AddGigViewModel
import com.example.gigappweather.ui.viewmodel.AppViewModel
import com.example.gigappweather.ui.viewmodel.GigDetailViewModel
import com.example.gigappweather.ui.viewmodel.GigListViewModel

class AppViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                connectivityObserver = ConnectivityObserver(context),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class GigListViewModelFactory(
    private val gigRepository: GigRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GigListViewModel::class.java)) {
            return GigListViewModel(
                gigRepository = gigRepository,
                weatherRepository = weatherRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class GigDetailViewModelFactory(
    private val gigId: Long,
    private val gigRepository: GigRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GigDetailViewModel::class.java)) {
            return GigDetailViewModel(
                gigId = gigId,
                gigRepository = gigRepository,
                weatherRepository = weatherRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class AddGigViewModelFactory(
    private val gigRepository: GigRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddGigViewModel::class.java)) {
            return AddGigViewModel(gigRepository = gigRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
