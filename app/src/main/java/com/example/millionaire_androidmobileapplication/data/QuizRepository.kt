package com.example.millionaire_androidmobileapplication.data

import android.util.Log
import com.example.millionaire_androidmobileapplication.MillionaireApp.Companion.database
import com.example.millionaire_androidmobileapplication.data.openApi.ApiClient
import com.example.millionaire_androidmobileapplication.domain.model.quiz.ChatRequest
import com.example.millionaire_androidmobileapplication.domain.model.quiz.Message
import com.example.millionaire_androidmobileapplication.domain.model.quiz.QuizQuestion
import com.example.millionaire_androidmobileapplication.domain.model.roomModels.QuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class QuizRepository {


    suspend fun getAllUsedQuestionTexts(): List<String> =
        withContext(Dispatchers.IO) {
            database.questionDao().getAllQuestions().map { it.text }
        }

    suspend fun getQuestionFromDb(level: Int, difficulty: String): QuizQuestion? =
        withContext(Dispatchers.IO) {
            val allQuestions = database.questionDao().getQuestionsByLevel(level, difficulty)
            allQuestions.shuffled().firstOrNull()?.let { entity ->
                QuizQuestion(entity.text, entity.options, entity.correctIndex, entity.difficulty)
            }
        }

    suspend fun saveQuestionToDb(question: QuizQuestion, level: Int) =
        withContext(Dispatchers.IO) {
            database.questionDao().insert(
                QuestionEntity(
                    text = question.text,
                    options = question.options,
                    correctIndex = question.correctIndex,
                    difficulty = question.difficulty,
                    level = level
                )
            )
        }


    suspend fun fetchQuestionFromApi(
        level: Int,
        difficulty: String,
        usedQuestions: Set<String>
    ): QuizQuestion? = withContext(Dispatchers.IO) {
        val systemPrompt = buildPrompt(difficulty, usedQuestions)

        val request = ChatRequest(
            messages = listOf(
                Message("system", systemPrompt),
                Message("user", "Generate question for level $level")
            )
        )

        try {
            val response = ApiClient.apiService.createChatCompletion(request)
            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
                parseQuestionJson(content, difficulty)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "API call failed", e)
            null
        }
    }


    private fun buildPrompt(difficulty: String, usedQuestions: Set<String>): String = """
        You are the question master for "Who Wants to Be a Millionaire".
        
        Generate EXACTLY ONE multiple-choice question with EXACTLY 4 options.
        
        STRICT RULES:
        - The question MUST be completely unique in topic, wording, and meaning.
        - DO NOT repeat, rephrase, or slightly modify any previous question.
        - If the new question is even slightly similar to any of the previous ones, DISCARD it and generate a completely different one.
        - Avoid common or overused trivia questions.
        
        PREVIOUS QUESTIONS (DO NOT REPEAT OR REPHRASE):
        ${usedQuestions.joinToString(separator = "\n")}
        
        FORMAT (STRICT JSON ONLY, NO EXTRA TEXT):
        {
          "text": "Question text here?",
          "options": ["Option A", "Option B", "Option C", "Option D"],
          "correct": 2
        }
        
        RULES FOR ANSWERS:
        - Exactly 4 options
        - Only ONE correct answer
        - Index starts from 0 (0-3)
        - Shuffle correct answer position randomly
        
        DIFFICULTY: $difficulty
        
        Make the question interesting, varied, and from different domains:
        (history, science, geography, pop culture, sports, etc.)
    """.trimIndent()

    private fun parseQuestionJson(raw: String, difficulty: String): QuizQuestion? {
        return try {
            val json = JSONObject(raw.trim())
            val text = json.getString("text")
            val options = json.getJSONArray("options").let { arr ->
                List(arr.length()) { arr.getString(it) }
            }
            val correct = json.getInt("correct")
            if (options.size != 4 || correct !in 0..3) return null
            QuizQuestion(text, options, correct, difficulty)
        } catch (e: Exception) {
            null
        }
    }

    fun buildFallbackQuestion(level: Int, difficulty: String): QuizQuestion =
        QuizQuestion(
            text = "Fallback question (level $level) – What is the capital of France?",
            options = listOf("Berlin", "Madrid", "Paris", "Rome"),
            correctIndex = 2,
            difficulty = difficulty
        )
}