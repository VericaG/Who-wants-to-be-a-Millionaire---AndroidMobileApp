package com.example.millionaire_androidmobileapplication

import android.app.Application
import androidx.room.Room
import com.example.millionaire_androidmobileapplication.data.room.AppDatabase

class MillionaireApp : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "millionaire_db"
        ).build()
    }
}
