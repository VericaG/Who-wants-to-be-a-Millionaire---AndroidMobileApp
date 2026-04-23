package com.example.millionaire_androidmobileapplication.domain.model.quiz

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)