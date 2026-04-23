package com.example.millionaire_androidmobileapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.millionaire_androidmobileapplication.domain.model.SoundManager
import com.example.millionaire_androidmobileapplication.ui.StartFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SoundManager.init(this)
        SoundManager.playBackground(this)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, StartFragment())
                setReorderingAllowed(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
    }
}
