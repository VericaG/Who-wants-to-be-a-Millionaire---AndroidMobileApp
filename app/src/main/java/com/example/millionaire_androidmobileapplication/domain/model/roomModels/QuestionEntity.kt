package com.example.millionaire_androidmobileapplication.domain.model.roomModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val difficulty: String,
    val level: Int
)
