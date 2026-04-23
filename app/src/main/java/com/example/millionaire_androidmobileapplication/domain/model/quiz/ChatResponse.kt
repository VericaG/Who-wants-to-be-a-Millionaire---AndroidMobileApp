package com.example.millionaire_androidmobileapplication.domain.model.quiz

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>?,
    val usage: Usage?
)