package com.example.gigappweather.core

sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>

    data class Error(
        val error: AppError,
        val cause: Throwable? = null,
    ) : Outcome<Nothing>
}

sealed interface AppError {
    data object Network : AppError

    data object CityNotFound : AppError
    data object NoForecastData : AppError

    data class Http(
        val code: Int,
        val message: String? = null,
    ) : AppError

    data object NotFound : AppError
    data object Serialization : AppError

    data class Unknown(
        val message: String? = null,
    ) : AppError
}
