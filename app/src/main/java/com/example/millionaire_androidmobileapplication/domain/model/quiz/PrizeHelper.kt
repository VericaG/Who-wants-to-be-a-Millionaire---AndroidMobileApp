package com.example.millionaire_androidmobileapplication.domain.model.quiz

val prizeMap = mapOf(
    1 to 100,
    2 to 200,
    3 to 300,
    4 to 500,
    5 to 1000,
    6 to 2000,
    7 to 4000,
    8 to 8000,
    9 to 16000,
    10 to 32000,
    11 to 64000,
    12 to 125000,
    13 to 250000,
    14 to 500000,
    15 to 1000000
)


fun getSafeAmount(level: Int, lastCorrect: Int): Int {
    return when {
        level < 5 -> 0
        level in 5..9 -> prizeMap[5] ?: 0
        level in 10..15 -> prizeMap[10] ?: 0
        else -> 0
    }
}

fun getQuitAmount(lastCorrect: Int): Int {
    return prizeMap[lastCorrect] ?: 0
}