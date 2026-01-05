package com.example.gigappweather.ui.navigation

object NavRoutes {
    const val LIST: String = "list"
    const val DETAIL: String = "detail/{gigId}"
    const val INFO: String = "info"

    fun detail(gigId: Long): String = "detail/$gigId"

    object Args {
        const val GIG_ID: String = "gigId"
    }
}
