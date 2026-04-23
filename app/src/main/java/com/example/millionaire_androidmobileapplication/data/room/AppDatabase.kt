package com.example.millionaire_androidmobileapplication.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.millionaire_androidmobileapplication.domain.model.roomModels.QuestionEntity

@Database(entities = [QuestionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}