//package com.example.millionaire_androidmobileapplication
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.millionaire_androidmobileapplication.MillionaireApp.Companion.database
//import com.example.millionaire_androidmobileapplication.data.openApi.ApiClient
//import com.example.millionaire_androidmobileapplication.domain.model.quiz.ChatRequest
//import com.example.millionaire_androidmobileapplication.domain.model.quiz.Message
//import com.example.millionaire_androidmobileapplication.domain.model.quiz.QuizQuestion
//import com.example.millionaire_androidmobileapplication.domain.model.roomModels.QuestionEntity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//
//class QuizViewModel : ViewModel() {
//
//    private val _currentQuestion = MutableLiveData<QuizQuestion?>()
//    val currentQuestion: LiveData<QuizQuestion?> = _currentQuestion
//
//    private val _currentLevel = MutableLiveData(1)
//    val currentLevel: LiveData<Int> = _currentLevel
//
//    private val _winnings = MutableLiveData(0L)
//    val winnings: LiveData<Long> = _winnings
//
//    private val usedQuestions = mutableSetOf<String>()
//
//    private val _lastSafeSum = MutableLiveData(0L)
//    val lastSafeSum: LiveData<Long> = _lastSafeSum
//
//    private val _shouldLoadQuestion = MutableLiveData(false)
//    val shouldLoadQuestion: LiveData<Boolean> = _shouldLoadQuestion
//
//    val isQuizActive = MutableLiveData(true)
//
//
//    val lifelinesUsed = mutableSetOf<String>()
//    val phoneNumbers = mutableListOf<String>()
//    var selectedPhone: String? = null
//
//
//    fun clearQuestion() {
//        _currentQuestion.value = null
//    }
//
//    private val prizeLadder = listOf(
//        0L, 100L, 200L, 300L, 500L, 1_000L,
//        2_000L, 4_000L, 8_000L, 16_000L, 32_000L,
//        64_000L, 125_000L, 250_000L, 500_000L, 1_000_000L
//    )
//
//    private val safeLevels = mapOf(
//        5 to 1_000L,
//        10 to 32_000L,
//        15 to 1_000_000L
//    )
//
//    init {
//        viewModelScope.launch {
//            loadUsedQuestionsFromDB()
//        }
//    }
//
//
//    fun requestLoadQuestion() {
//        _shouldLoadQuestion.value = true
//    }
//
//    fun onQuestionLoaded() {
//        _shouldLoadQuestion.value = false
//    }
//
//    private suspend fun loadUsedQuestionsFromDB() {
//        val all = withContext(Dispatchers.IO) {
//            database.questionDao().getAllQuestions()
//        }
//        usedQuestions.addAll(all.map { it.text })
//    }
//
//    private suspend fun getUniqueQuestionFromDB(level: Int, difficulty: String): QuizQuestion? {
//        val allQuestions = database.questionDao().getQuestionsByLevel(level, difficulty)
//
//        val unused = allQuestions.filter { it.text !in usedQuestions }
//
//        val question = if (unused.isNotEmpty()) {
//            unused.shuffled().first()
//        } else if (allQuestions.isNotEmpty()) {
//            allQuestions.shuffled().first()
//        } else {
//            null
//        }
//
//        question?.let { usedQuestions.add(it.text) }
//
//        return question?.let {
//            QuizQuestion(it.text, it.options, it.correctIndex, it.difficulty)
//        }
//    }
//
//
//    fun calculateWinnings(level: Int): Long = prizeLadder.getOrElse(level) { 0L }
//
//    fun updateSafeSum(level: Int = _currentLevel.value ?: 1) {
//        _lastSafeSum.value = when {
//            level >= 10 -> 32_000L
//            level >= 5 -> 1_000L
//            else -> 0L
//        }
//    }
//
//    fun answerCorrect() {
//        val level = _currentLevel.value ?: 1
//        val next = level + 1
//        _currentLevel.value = next
//        _winnings.value = calculateWinnings(level)
//        updateSafeSum(level)
//        fetchQuestion(next)
//    }
//
//    fun quitGame(): Long = _winnings.value ?: 0L
//    fun wrongAnswer(): Long = _lastSafeSum.value ?: 0L
//
//    //    fun fetchQuestion(level: Int) {
////        viewModelScope.launch {
////            //      if (usedQuestions.isEmpty()) {
////            loadUsedQuestionsFromDB()
////            //   }
////
////            val difficulty = when {
////                level <= 5 -> "easy (high-school/general knowledge level)"
////                level <= 10 -> "medium (university / good general knowledge)"
////                else -> "very hard (expert, tricky, specialized knowledge)"
////            }
////
////            //  First -DB
////            val dbQuestion = withContext(Dispatchers.IO) { getUniqueQuestionFromDB(level, difficulty) }
////            if (dbQuestion != null) {
////                _currentQuestion.postValue(dbQuestion)
////                return@launch
////            }
////
////            //  If Db is empty -> call API
////            try {
////                val systemPrompt = """
////                You are the question master for "Who Wants to Be a Millionaire".
////                Create ONE question with EXACTLY 4 answer options.
////                Create a new question that has NOT been asked before in this game session.
////                Ensure the question is unique.
////                Return **ONLY** valid JSON – no extra text, no markdown, no explanation:
////                {
////                  "text": "Question text here?",
////                  "options": ["A text", "B text", "C text", "D text"],
////                  "correct": 2
////                }
////                Difficulty: $difficulty
////                Vary topics – history, science, geography, pop culture, sports, etc.
////            """.trimIndent()
////
////                val request = ChatRequest(
////                    messages = listOf(
////                        Message("system", systemPrompt),
////                        Message("user", "Generate question for level $level")
////                    )
////                )
////
////                val response = ApiClient.apiService.createChatCompletion(request)
////
////                if (response.isSuccessful) {
////                    val content = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
////                    val parsed = parseQuestionJson(content, difficulty)
////
////                    if (parsed != null && !usedQuestions.contains(parsed.text)) {
////                        usedQuestions.add(parsed.text)
////                        _currentQuestion.postValue(parsed)
////
////                        // Save to DB
////                        withContext(Dispatchers.IO) {
////                            database.questionDao().insert(
////                                QuestionEntity(
////                                    text = parsed.text,
////                                    options = parsed.options,
////                                    correctIndex = parsed.correctIndex,
////                                    difficulty = parsed.difficulty,
////                                    level = level
////                                )
////                            )
////                        }
////                        return@launch
////                    }
////                }
////
////                throw Exception("API failed or returned invalid question")
////            } catch (e: Exception) {
////                Log.e("QuizViewModel", "Exception fetching question from API", e)
////
////                // If API fails and DB is empty → fallback
////                postFallbackQuestion(level, difficulty)
////            }
////        }
////    }
//
//
//    //RABOTI SO FLICK
////    fun fetchQuestion(level: Int, retries: Int = 0) {
////        viewModelScope.launch {
////            val difficulty = when {
////                level <= 5 -> "easy (high-school/general knowledge level)"
////                level <= 10 -> "medium (university / good general knowledge)"
////                else -> "very hard (expert, tricky, specialized knowledge)"
////            }
////
////            try {
////                val systemPrompt = """
////You are the question master for "Who Wants to Be a Millionaire".
////Create ONE question with EXACTLY 4 answer options.
////The question MUST be unique in wording, topic, and options.
////Do NOT repeat or rephrase any of these: ${usedQuestions.joinToString()}
////If the generated question overlaps with them, discard it and create a different one.
////Return ONLY valid JSON – no extra text, no markdown, no explanation:
////{
////  "text": "Question text here?",
////  "options": ["A text", "B text", "C text", "D text"],
////  "correct": 2
////}
////Difficulty: $difficulty
////Vary topics – history, science, geography, pop culture, sports, etc.
////""".trimIndent()
////
////                val request = ChatRequest(
////                    messages = listOf(
////                        Message("system", systemPrompt),
////                        Message("user", "Generate question for level $level")
////                    )
////                )
////
////                val response = ApiClient.apiService.createChatCompletion(request)
////
////                if (response.isSuccessful) {
////                    val content = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
////                    val parsed = parseQuestionJson(content, difficulty)
////
////                    if (parsed != null) {
////                        // проверка за дупликат
////                        if (usedQuestions.contains(parsed.text)) {
//////                            if (retries < 3) {
//////                                Log.w("QuizViewModel", "Duplicate detected, retrying...")
//////                                fetchQuestion(level, retries + 1)
//////                                return@launch
//////                            }
////                            if (parsed != null && !usedQuestions.contains(parsed.text)) {
////                                usedQuestions.add(parsed.text)
////                                _currentQuestion.postValue(parsed)
////                                return@launch
////                            } else {
////                                Log.e("QuizViewModel", "Too many duplicates, using anyway")
////                                _currentQuestion.postValue(parsed)
////                                return@launch
////                            }
////                        }
////                        usedQuestions.add(parsed.text)
////                        _currentQuestion.postValue(parsed)
////                        return@launch
////                    }
////                }
////
////                // ако API не врати валидно прашање
////                if (retries < 3) {
////                    Log.w("QuizViewModel", "Invalid question, retrying...")
////                    fetchQuestion(level, retries + 1)
////                } else {
////                    Log.e("QuizViewModel", "API failed after retries, no question set")
////                }
////
////            } catch (e: Exception) {
////                Log.e("QuizViewModel", "Exception fetching question from API", e)
////                // НЕ користиме fallback ако сакаш само API прашања
////            }
////        }
////    }
//
//
//    fun fetchQuestion(level: Int) {
//
//
//        viewModelScope.launch {
//
//            val difficulty = when {
//                level <= 5 -> "easy (high-school/general knowledge level)"
//                level <= 10 -> "medium (university / good general knowledge)"
//                else -> "very hard (expert, tricky, specialized knowledge)"
//            }
//            val systemPrompt = """
//            You are the question master for "Who Wants to Be a Millionaire".
//
//            Generate EXACTLY ONE multiple-choice question with EXACTLY 4 options.
//
//            STRICT RULES:
//            - The question MUST be completely unique in topic, wording, and meaning.
//            - DO NOT repeat, rephrase, or slightly modify any previous question.
//            - If the new question is even slightly similar to any of the previous ones, DISCARD it and generate a completely different one.
//            - Avoid common or overused trivia questions.
//
//            PREVIOUS QUESTIONS (DO NOT REPEAT OR REPHRASE):
//            ${usedQuestions.joinToString(separator = "\n")}
//
//            FORMAT (STRICT JSON ONLY, NO EXTRA TEXT):
//            {
//              "text": "Question text here?",
//              "options": ["Option A", "Option B", "Option C", "Option D"],
//              "correct": 2
//            }
//
//            RULES FOR ANSWERS:
//            - Exactly 4 options
//            - Only ONE correct answer
//            - Index starts from 0 (0-3)
//            - Shuffle correct answer position randomly
//
//            DIFFICULTY: $difficulty
//
//            Make the question interesting, varied, and from different domains:
//            (history, science, geography, pop culture, sports, etc.)
//            """.trimIndent()
//
//            val request = ChatRequest(
//                messages = listOf(
//                    Message("system", systemPrompt),
//                    Message("user", "Generate question for level $level")
//                )
//            )
//
//            repeat(5) { attempt ->
//
//                try {
//                    val response = ApiClient.apiService.createChatCompletion(request)
//
//                    if (response.isSuccessful) {
//                        val content =
//                            response.body()?.choices?.firstOrNull()?.message?.content ?: ""
//                        val parsed = parseQuestionJson(content, difficulty)
//
//                        if (parsed != null && !usedQuestions.contains(parsed.text)) {
//                            usedQuestions.add(parsed.text)
//
//                            _currentQuestion.postValue(parsed)
//
//                            Log.d("QuizViewModel", "Question loaded (attempt $attempt)")
//                            return@launch
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    Log.e("QuizViewModel", "Error fetching question", e)
//                }
//            }
//
//            Log.e("QuizViewModel", "Failed to get unique question after retries")
//            postFallbackQuestion(level, difficulty)
//        }
//    }
//
//
//    private fun parseQuestionJson(raw: String, difficulty: String): QuizQuestion? {
//        return try {
//            val json = JSONObject(raw.trim())
//            val text = json.getString("text")
//            val options = json.getJSONArray("options").let { arr ->
//                List(arr.length()) { arr.getString(it) }
//            }
//            val correct = json.getInt("correct")
//            if (options.size != 4 || correct !in 0..3) return null
//            QuizQuestion(text, options, correct, difficulty)
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    private fun postFallbackQuestion(level: Int, difficulty: String) {
//        _currentQuestion.postValue(
//            QuizQuestion(
//                text = "Fallback question (level $level) – What is the capital of France?",
//                options = listOf("Berlin", "Madrid", "Paris", "Rome"),
//                correctIndex = 2,
//                difficulty = difficulty
//            )
//        )
//    }
//
//    fun nextLevel() {
//        val level = _currentLevel.value ?: 1
//        _winnings.value = calculateWinnings(level)
//        updateSafeSum(level)
//        val next = level + 1
//        if (next <= 15) {
//            _currentLevel.value = next
//            fetchQuestion(next)
//        }
//    }
//
//
//    fun levelUpWithoutQuestion() {
//        val current = _currentLevel.value ?: 1
//        val next = current + 1
//
//        if (next <= 15) {
//            _winnings.value = calculateWinnings(current)
//            updateSafeSum(current)
//            _currentLevel.value = next
//            _currentQuestion.value = null
//        }
//    }
//}
//
//


package com.example.millionaire_androidmobileapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.millionaire_androidmobileapplication.data.QuizRepository
import com.example.millionaire_androidmobileapplication.domain.model.quiz.QuizQuestion
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val repository = QuizRepository()

    private val _currentQuestion = MutableLiveData<QuizQuestion?>()
    val currentQuestion: LiveData<QuizQuestion?> = _currentQuestion

    private val _currentLevel = MutableLiveData(1)
    val currentLevel: LiveData<Int> = _currentLevel

    private val _winnings = MutableLiveData(0L)
    val winnings: LiveData<Long> = _winnings

    private val _lastSafeSum = MutableLiveData(0L)
    val lastSafeSum: LiveData<Long> = _lastSafeSum

    private val _shouldLoadQuestion = MutableLiveData(false)
    val shouldLoadQuestion: LiveData<Boolean> = _shouldLoadQuestion

    val isQuizActive = MutableLiveData(true)


    private val usedQuestions = mutableSetOf<String>()

    val lifelinesUsed = mutableSetOf<String>()
    val phoneNumbers = mutableListOf<String>()
    var selectedPhone: String? = null


    private val prizeLadder = listOf(
        0L, 100L, 200L, 300L, 500L, 1_000L,
        2_000L, 4_000L, 8_000L, 16_000L, 32_000L,
        64_000L, 125_000L, 250_000L, 500_000L, 1_000_000L
    )


    init {
        viewModelScope.launch {
            loadUsedQuestionsFromDB()
        }
    }


    fun clearQuestion() {
        _currentQuestion.value = null
    }

    fun requestLoadQuestion() {
        _shouldLoadQuestion.value = true
    }

    fun onQuestionLoaded() {
        _shouldLoadQuestion.value = false
    }

    fun calculateWinnings(level: Int): Long = prizeLadder.getOrElse(level) { 0L }

    fun updateSafeSum(level: Int = _currentLevel.value ?: 1) {
        _lastSafeSum.value = when {
            level >= 10 -> 32_000L
            level >= 5 -> 1_000L
            else -> 0L
        }
    }

    fun answerCorrect() {
        val level = _currentLevel.value ?: 1
        val next = level + 1
        _currentLevel.value = next
        _winnings.value = calculateWinnings(level)
        updateSafeSum(level)
        fetchQuestion(next)
    }

    fun quitGame(): Long = _winnings.value ?: 0L
    fun wrongAnswer(): Long = _lastSafeSum.value ?: 0L

    fun nextLevel() {
        val level = _currentLevel.value ?: 1
        _winnings.value = calculateWinnings(level)
        updateSafeSum(level)
        val next = level + 1
        if (next <= 15) {
            _currentLevel.value = next
            fetchQuestion(next)
        }
    }

    fun levelUpWithoutQuestion() {
        val current = _currentLevel.value ?: 1
        val next = current + 1
        if (next <= 15) {
            _winnings.value = calculateWinnings(current)
            updateSafeSum(current)
            _currentLevel.value = next
            _currentQuestion.value = null
        }
    }


    fun fetchQuestion(level: Int) {
        viewModelScope.launch {
            val difficulty = difficultyForLevel(level)

            repeat(5) { attempt ->
                val question = repository.fetchQuestionFromApi(level, difficulty, usedQuestions)

                if (question != null && !usedQuestions.contains(question.text)) {
                    usedQuestions.add(question.text)
                    _currentQuestion.postValue(question)

                    repository.saveQuestionToDb(question, level)

                    Log.d("QuizViewModel", "Question loaded from API (attempt $attempt)")
                    return@launch
                }
            }

            Log.w("QuizViewModel", "API attempts exhausted, falling back to DB")
            val dbQuestion = repository.getQuestionFromDb(level, difficulty)
            if (dbQuestion != null && !usedQuestions.contains(dbQuestion.text)) {
                usedQuestions.add(dbQuestion.text)
                _currentQuestion.postValue(dbQuestion)
                return@launch
            }

            Log.e("QuizViewModel", "No question available, using fallback")
            _currentQuestion.postValue(repository.buildFallbackQuestion(level, difficulty))
        }
    }


    private suspend fun loadUsedQuestionsFromDB() {
        val texts = repository.getAllUsedQuestionTexts()
        usedQuestions.addAll(texts)
    }

    private fun difficultyForLevel(level: Int): String = when {
        level <= 5 -> "easy (high-school/general knowledge level)"
        level <= 10 -> "medium (university / good general knowledge)"
        else -> "very hard (expert, tricky, specialized knowledge)"
    }
}