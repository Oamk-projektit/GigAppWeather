package com.example.gigappweather.domain.model

import com.example.gigappweather.R

object FinnishCities {
    val all: List<FinnishCity> = listOf(
        FinnishCity("helsinki", R.string.city_helsinki, 60.1699, 24.9384),
        FinnishCity("espoo", R.string.city_espoo, 60.2055, 24.6559),
        FinnishCity("vantaa", R.string.city_vantaa, 60.2934, 25.0378),
        FinnishCity("tampere", R.string.city_tampere, 61.4978, 23.7610),
        FinnishCity("turku", R.string.city_turku, 60.4518, 22.2666),
        FinnishCity("oulu", R.string.city_oulu, 65.0121, 25.4651),
        FinnishCity("jyvaskyla", R.string.city_jyvaskyla, 62.2426, 25.7473),
        FinnishCity("lahti", R.string.city_lahti, 60.9827, 25.6615),
        FinnishCity("kuopio", R.string.city_kuopio, 62.8924, 27.6770),
        FinnishCity("pori", R.string.city_pori, 61.4850, 21.7970),
        FinnishCity("vaasa", R.string.city_vaasa, 63.0951, 21.6165),
        FinnishCity("lappeenranta", R.string.city_lappeenranta, 61.0583, 28.1861),
        FinnishCity("joensuu", R.string.city_joensuu, 62.6010, 29.7636),
        FinnishCity("seinajoki", R.string.city_seinajoki, 62.7903, 22.8403),
        FinnishCity("rovaniemi", R.string.city_rovaniemi, 66.5039, 25.7294),
    )

    fun byId(id: String): FinnishCity? = all.firstOrNull { it.id == id }
}
