package com.example.millionaire_androidmobileapplication.data.room
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.millionaire_androidmobileapplication.domain.model.roomModels.QuestionEntity

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE level = :level AND difficulty = :difficulty")
    suspend fun getQuestionsByLevel(level: Int, difficulty: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Query("DELETE FROM questions")
    fun deleteAll()
}
