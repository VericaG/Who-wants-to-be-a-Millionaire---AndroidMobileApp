package com.example.millionaire_androidmobileapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.example.millionaire_androidmobileapplication.R
class StartFragment : Fragment(R.layout.fragment_start) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnStart = view.findViewById<Button>(R.id.btn_start_game)
        btnStart.setOnClickListener {
            btnStart.postDelayed({
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, QuizFragment())
                    .addToBackStack(null)
                    .commit()
            }, 250)
        }
    }
}


