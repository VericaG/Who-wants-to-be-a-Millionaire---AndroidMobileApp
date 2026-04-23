package com.example.millionaire_androidmobileapplication.domain.model.quiz

data class ChatRequest(
    val model: String = "gpt-4o-mini-2024-07-18",    val messages: List<Message>,
    val temperature: Double = 0.5,
    val max_tokens: Int = 450,
    val top_p: Double = 1.0,
    val frequency_penalty: Double = 0.5,
    val presence_penalty: Double = 0.3
)