package com.example.millionaire_androidmobileapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.millionaire_androidmobileapplication.MainActivity
import com.example.millionaire_androidmobileapplication.R

class EndActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        val tvTitle = findViewById<TextView>(R.id.tv_end_title)
        val tvMessage = findViewById<TextView>(R.id.tv_end_message)
        val tvSum = findViewById<TextView>(R.id.tv_end_sum)
        val tvLevel = findViewById<TextView>(R.id.tv_end_level)
        val btnNewGame = findViewById<Button>(R.id.btn_new_game)

        val wonSum = intent.getLongExtra("WON_SUM", 0)
        val message = intent.getStringExtra("MESSAGE") ?: "Game Over"
        val levelReached = intent.getIntExtra("LEVEL", 1)
        val isWinner = intent.getBooleanExtra("IS_WINNER", false)

        tvMessage.text = message
        tvSum.text = formatSum(wonSum)
        tvLevel.text = "Level reached: $levelReached"

        if (isWinner) {
            tvTitle.text = "Congratulations! You Are A Millionare!"
            tvTitle.setTextColor(0xFFFFD700.toInt())
        }

        btnNewGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun formatSum(amount: Long): String {
        return when {
            amount >= 1_000_000L -> "1 000 000 ден."
            amount >= 500_000L -> "500 000 ден."
            amount >= 250_000L -> "250 000 ден."
            amount >= 125_000L -> "125 000 ден."
            else -> String.format("$%,d", amount).replace(",", " ")
        }
    }

}