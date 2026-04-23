package com.example.millionaire_androidmobileapplication.domain.model.quiz


//@Entity(tableName = "questions")
data class QuizQuestion(
//    @PrimaryKey(autoGenerate = true)
//    val id : Long = 1,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val difficulty: String
)