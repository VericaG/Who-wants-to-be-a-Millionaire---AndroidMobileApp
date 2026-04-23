package com.example.millionaire_androidmobileapplication.data.openApi

import com.example.millionaire_androidmobileapplication.BuildConfig
import com.example.millionaire_androidmobileapplication.domain.model.quiz.ChatRequest
import com.example.millionaire_androidmobileapplication.domain.model.quiz.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
