package com.example.millionaire_androidmobileapplication.domain.model.quiz

data class Message(
    val role: String, // "system" / "user" / "assistant"
    val content: String
)