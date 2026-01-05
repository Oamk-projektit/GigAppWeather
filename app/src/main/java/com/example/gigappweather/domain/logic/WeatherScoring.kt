package com.example.gigappweather.domain.logic

import com.example.gigappweather.R
import kotlin.math.roundToInt

object WeatherScoring {

    /**
     * Returns a score in range 0..100. Higher is better.
     */
    fun computeWeatherScore(
        isOutdoor: Boolean,
        tempMax: Double,
        precipitationSum: Double,
        windMax: Double,
    ): Int {
        val breakdown = computeScoreBreakdown(
            isOutdoor = isOutdoor,
            tempMax = tempMax,
            precipitationSum = precipitationSum,
            windMax = windMax,
        )
        return breakdown.score
    }

    /**
     * Maps a score to a string resource id.
     */
    fun recommendationTextRes(score: Int): Int {
        return when {
            score >= 80 -> R.string.recommendation_great
            score >= 50 -> R.string.recommendation_ok
            else -> R.string.recommendation_bad
        }
    }

    fun computeScoreBreakdown(
        isOutdoor: Boolean,
        tempMax: Double,
        precipitationSum: Double,
        windMax: Double,
    ): WeatherScoreBreakdown {
        val tempPenalty = if (isOutdoor) {
            when {
                tempMax < -10 -> 40
                tempMax < 0 -> 25
                tempMax < 5 -> 12
                tempMax > 30 -> 18
                tempMax > 25 -> 10
                else -> 0
            }
        } else {
            when {
                tempMax < -10 -> 15
                tempMax < 0 -> 10
                tempMax > 30 -> 8
                else -> 0
            }
        }

        val precipitationPenaltyRaw = (precipitationSum * if (isOutdoor) 12 else 6).roundToInt()
        val precipitationPenalty = precipitationPenaltyRaw.coerceIn(0, if (isOutdoor) 60 else 35)

        val windPenalty = if (isOutdoor) {
            when {
                windMax >= 20 -> 35
                windMax >= 14 -> 22
                windMax >= 10 -> 12
                else -> 0
            }
        } else {
            when {
                windMax >= 20 -> 15
                windMax >= 14 -> 10
                else -> 0
            }
        }

        val totalPenalty = tempPenalty + precipitationPenalty + windPenalty
        val score = (100 - totalPenalty).coerceIn(0, 100)

        return WeatherScoreBreakdown(
            isOutdoor = isOutdoor,
            tempMax = tempMax,
            precipitationSum = precipitationSum,
            windMax = windMax,
            tempPenalty = tempPenalty,
            precipitationPenalty = precipitationPenalty,
            windPenalty = windPenalty,
            score = score,
        )
    }
}

data class WeatherScoreBreakdown(
    val isOutdoor: Boolean,
    val tempMax: Double,
    val precipitationSum: Double,
    val windMax: Double,
    val tempPenalty: Int,
    val precipitationPenalty: Int,
    val windPenalty: Int,
    val score: Int,
)
