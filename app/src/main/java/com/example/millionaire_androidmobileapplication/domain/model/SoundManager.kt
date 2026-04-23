package com.example.millionaire_androidmobileapplication.domain.model

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.millionaire_androidmobileapplication.R

object SoundManager {

    private lateinit var soundPool: SoundPool
    private var correctSound: Int = 0
    private var wrongSound: Int = 0

    private var backgroundPlayer: MediaPlayer? = null
    private var lifelineTimerPlayer: MediaPlayer? = null

    fun init(context: Context) {
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        correctSound = soundPool.load(context, R.raw.correct_answer, 1)
        wrongSound = soundPool.load(context, R.raw.wrong_answer, 1)
    }

    fun playCorrect() {
        soundPool.play(correctSound, 1f, 1f, 0, 0, 1f)
    }

    fun playWrong() {
        soundPool.play(wrongSound, 1f, 1f, 0, 0, 1f)
    }

    fun playBackground(context: Context) {
        stopBackground()
        backgroundPlayer = MediaPlayer.create(context, R.raw.background_loop)
        backgroundPlayer?.isLooping = true
        backgroundPlayer?.start()
    }

    fun pauseBackground() {
        backgroundPlayer?.pause()
    }

    fun resumeBackground() {
        backgroundPlayer?.start()
    }

    fun stopBackground() {
        backgroundPlayer?.stop()
        backgroundPlayer?.release()
        backgroundPlayer = null
    }

    fun playPrizeChange(context: Context) {
        val prizeSound = MediaPlayer.create(context, R.raw.prize_change)
        prizeSound.setOnCompletionListener {
            it.release()
        }
        prizeSound.start()
    }

    fun playLifelineTimer(context: Context) {
        stopLifelineTimer()
        lifelineTimerPlayer = MediaPlayer.create(context, R.raw.lifeline_timer)
        lifelineTimerPlayer?.isLooping = true
        lifelineTimerPlayer?.start()
    }

    fun stopLifelineTimer() {
        lifelineTimerPlayer?.stop()
        lifelineTimerPlayer?.release()
        lifelineTimerPlayer = null
    }

    fun release() {
        soundPool.release()
        stopBackground()
        stopLifelineTimer()
    }
}
