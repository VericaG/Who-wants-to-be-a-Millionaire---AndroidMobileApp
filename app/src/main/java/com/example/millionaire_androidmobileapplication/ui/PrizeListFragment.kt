package com.example.millionaire_androidmobileapplication.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.millionaire_androidmobileapplication.R
import android.os.Handler
import com.example.millionaire_androidmobileapplication.QuizViewModel
import com.example.millionaire_androidmobileapplication.domain.model.SoundManager

class PrizeListFragment : Fragment(R.layout.fragment_prize_list) {

    private val viewModel: QuizViewModel by activityViewModels()

    private var oldLevel: Int = 1
    private var newLevel: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            oldLevel = it.getInt(ARG_OLD_LEVEL)
            newLevel = it.getInt(ARG_NEW_LEVEL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val levels = listOf(
            view.findViewById<TextView>(R.id.level1),
            view.findViewById<TextView>(R.id.level2),
            view.findViewById<TextView>(R.id.level3),
            view.findViewById<TextView>(R.id.level4),
            view.findViewById<TextView>(R.id.level5),
            view.findViewById<TextView>(R.id.level6),
            view.findViewById<TextView>(R.id.level7),
            view.findViewById<TextView>(R.id.level8),
            view.findViewById<TextView>(R.id.level9),
            view.findViewById<TextView>(R.id.level10),
            view.findViewById<TextView>(R.id.level11),
            view.findViewById<TextView>(R.id.level12),
            view.findViewById<TextView>(R.id.level13),
            view.findViewById<TextView>(R.id.level14),
            view.findViewById<TextView>(R.id.level15)
        )

        viewModel.currentLevel.observe(viewLifecycleOwner) { level ->
            levels.forEachIndexed { index, textView ->
                textView.setTextColor(
                    if (index + 1 == level) Color.YELLOW else Color.WHITE
                )
            }
        }

        val currentLevelView = levels[oldLevel - 1]
        val nextLevelView = levels[newLevel - 1]

        animateLevel(nextLevelView)
        SoundManager.playPrizeChange(requireContext())

        parentFragmentManager.popBackStack()

        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.popBackStack()
        }, 1200)


        fun animatePrize(textView: TextView) {
            val colorAnim = ObjectAnimator.ofArgb(
                textView,
                "textColor",
                Color.WHITE,
                Color.YELLOW
            ).apply {
                duration = 1200
            }

            val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.3f, 1f)
            val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.3f, 1f)
            scaleX.duration = 1200
            scaleY.duration = 1200

            AnimatorSet().apply {
                playTogether(colorAnim, scaleX, scaleY)
                start()
            }
        }

    }

    companion object {
        private const val ARG_OLD_LEVEL = "old_level"
        private const val ARG_NEW_LEVEL = "new_level"
        fun newInstance(oldLevel: Int, newLevel: Int) = PrizeListFragment().apply {
            arguments =
                Bundle().apply {
                    putInt(ARG_OLD_LEVEL, oldLevel)
                    putInt(ARG_NEW_LEVEL, newLevel)
                }
        }
    }

    private fun animateLevel(textView: TextView) {
        val colorAnim = ObjectAnimator.ofArgb(
            textView,
            "textColor",
            Color.WHITE,
            Color.YELLOW
        ).apply {
            duration = 1200
        }


        val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f, 1f)
        scaleX.duration = 1200
        scaleY.duration = 1200

        AnimatorSet().apply {
            playTogether(colorAnim, scaleX, scaleY)
            start()
        }
    }
}

