//package com.example.millionaire_androidmobileapplication.ui
//
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.view.View
//import android.widget.TextView
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import com.example.millionaire_androidmobileapplication.R
//import com.example.millionaire_androidmobileapplication.domain.model.quiz.QuizQuestion
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import android.Manifest
//import android.content.ContentValues.TAG
//import android.provider.ContactsContract
//import android.util.Log
//import android.widget.ImageButton
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.millionaire_androidmobileapplication.EndActivity
//import com.example.millionaire_androidmobileapplication.QuizViewModel
//import com.example.millionaire_androidmobileapplication.domain.model.SoundManager
//import com.google.android.material.button.MaterialButton
//
//
//class QuizFragment : Fragment(R.layout.fragment_quiz) {
//
//    private val viewModel: QuizViewModel by activityViewModels()
//
//    private lateinit var tvQuestion: TextView
//    private lateinit var tvLevel: TextView
//    private lateinit var tvWinnings: TextView
//    private lateinit var tvTimer: TextView
//
//    private lateinit var btnA: MaterialButton
//    private lateinit var btnB: MaterialButton
//    private lateinit var btnC: MaterialButton
//    private lateinit var btnD: MaterialButton
//
//    private lateinit var btn5050: ImageButton
//    private lateinit var btnPhone: ImageButton
//    private lateinit var btnGoogle: ImageButton
//    private lateinit var btnQuit: ImageButton
//
//    private var lifelineTimer: CountDownTimer? = null
//
//    private var isLifelineTimerRunning = false
//
//
//
//    private val requestCallPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) {
//                viewModel.selectedPhone?.let { phone ->
//                    startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
//                }
//            }
//        }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(
//                v.paddingLeft,
//                systemBars.top,
//                v.paddingRight,
//                systemBars.bottom
//            )
//            insets
//        }
//
//        bindViews(view)
//        observeViewModel()
//        setupListeners()
//
//        if ("50:50" in viewModel.lifelinesUsed) {
//            btn5050.isEnabled = false
//            btn5050.alpha = 0.5f
//        }
//        if ("Google" in viewModel.lifelinesUsed) {
//            btnGoogle.isEnabled = false
//            btnGoogle.alpha = 0.5f
//        }
//        if ("PhoneFriend" in viewModel.lifelinesUsed) {
//            btnPhone.isEnabled = false
//            btnPhone.alpha = 0.5f
//        }
//
////        if (viewModel.currentQuestion.value == null) {
////            viewModel.fetchQuestion(1)
////        }
//    }
//
//    private lateinit var optionButtons: List<MaterialButton>
//
//    private fun bindViews(view: View) {
//        tvQuestion = view.findViewById(R.id.tv_question)
//        tvLevel = view.findViewById(R.id.tv_level)
//        tvWinnings = view.findViewById(R.id.tv_winnings)
//        tvTimer = view.findViewById(R.id.tv_timer)
//
//        btnA = view.findViewById(R.id.btn_option_a)
//        btnB = view.findViewById(R.id.btn_option_b)
//        btnC = view.findViewById(R.id.btn_option_c)
//        btnD = view.findViewById(R.id.btn_option_d)
//
//        btn5050 = view.findViewById(R.id.btn_5050)
//        btnPhone = view.findViewById(R.id.btn_phone)
//        btnGoogle = view.findViewById(R.id.btn_google)
//        btnQuit = view.findViewById(R.id.btn_quit)
//
//        optionButtons = listOf(btnA, btnB, btnC, btnD)
//    }
//
//
//    private fun observeViewModel() {
//        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
//            if (question != null) {
//                displayQuestion(question)
//            }
//        }
//
//
//        viewModel.currentLevel.observe(viewLifecycleOwner) {
//            tvLevel.text = "Level: $it / 15"
//        }
//
//        viewModel.winnings.observe(viewLifecycleOwner) {
//            tvWinnings.text = "Winnings: $${it.format()}"
//        }
//
//        viewModel.shouldLoadQuestion.observe(viewLifecycleOwner) { shouldLoad ->
//            if (shouldLoad) {
//                val level = viewModel.currentLevel.value ?: return@observe
//                viewModel.fetchQuestion(level)
//                viewModel.onQuestionLoaded()
//            }
//        }
//        Log.d("QUIZ_FLOW", "QuizFragment ACTIVE, loading question")
//    }
//
//    private fun loadContacts() {
//        val contacts = mutableListOf<String>()
//
//        val cursor = requireContext().contentResolver.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            arrayOf(
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//            ),
//            null,
//            null,
//            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//        )
//
//        cursor?.use {
//            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
//            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//
//            while (it.moveToNext()) {
//                val name = it.getString(nameIndex)
//                val number = it.getString(numberIndex)
//                contacts.add("$name: $number")
//            }
//        }
//
//        viewModel.phoneNumbers.clear()
//        viewModel.phoneNumbers.addAll(contacts)
//    }
//
//    private fun showContactsDialog() {
//
//        loadContacts()
//
//
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Phone a Friend")
//            .setItems(viewModel.phoneNumbers.toTypedArray()) { _, which ->
//                val selected = viewModel.phoneNumbers[which]
//                val number = selected.substringAfter(": ").trim()
//
//                startLifelineTimer()
//
//                SoundManager.playLifelineTimer(requireContext())
//
//                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
//                startActivity(callIntent)
//            }
//            .show()
//    }
//
//
//    private val requestContactsPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) {
//                loadContacts()
//                showContactsDialog()
//            }
//        }
//
//
//    private fun displayQuestion(q: QuizQuestion) {
//
//        resetOptionButtons()
//
//        tvQuestion.text = q.text
//
//        optionButtons.forEachIndexed { index, button ->
//            button.text = "${'A' + index}: ${q.options[index]}"
//            button.isEnabled = true
//            Log.d(TAG, "Setting button ${'A' + index} -> ${q.options[index]}")
//
//
//            button.visibility = View.VISIBLE
//
//            button.setBackgroundResource(R.drawable.bg_answer_selector)
//            button.backgroundTintList = null
//
//            button.alpha = 0f
//            button.scaleX = 0.8f
//            button.scaleY = 0.8f
//
//            button.animate()
//                .alpha(1f)
//                .scaleX(1f)
//                .scaleY(1f)
//                .setDuration(1600)
//                .start()
//        }
//
//        if (!isLifelineTimerRunning) {
//            tvTimer.visibility = View.GONE
//        }
//
//
//    }
//
//
//    private fun setupListeners() {
//        optionButtons.forEachIndexed { index, button ->
//            button.setOnClickListener { checkAnswer(index) }
//        }
//
//        btn5050.setOnClickListener {
//            use5050()
//            startLifelineTimer()
//            btn5050.isEnabled = false
//            btn5050.alpha = 0.5f
//        }
//
//        btnPhone.setOnClickListener {
//            usePhoneFriend()
//            btnPhone.isEnabled = false
//            btnPhone.alpha = 0.5f
//        }
//
//        btnGoogle.setOnClickListener {
//            useGoogleSearch()
//            startLifelineTimer()
//            btnGoogle.isEnabled = false
//            btnGoogle.alpha = 0.5f
//        }
//
//
//        btnQuit.setOnClickListener {
//            btnQuit.isEnabled = false
//            btnQuit.postDelayed({
//                endGame(
//                    viewModel.quitGame(),
//                    "You quit the game"
//                )
//            }, 1200)
//        }
//
//    }
//
//
//    private fun checkAnswer(selectedIndex: Int) {
//        val q = viewModel.currentQuestion.value ?: return
//        optionButtons.forEach { it.isEnabled = false }
//
//        stopLifelineTimer()
//
//        Log.d(TAG, "displayQuestion() CALLED")
//        Log.d(TAG, "Options = ${q.options}")
//
//        optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_pressed)
//        val isCorrect = selectedIndex == q.correctIndex
//
//        tvQuestion.postDelayed({
//            val oldLevel = viewModel.currentLevel.value ?: 1
////            if (isCorrect) {
////                SoundManager.playCorrect()
////                optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_correct)
////                tvQuestion.postDelayed(
////                    {
////                        viewModel.levelUpWithoutQuestion()
////                        showPrizeList(oldLevel, oldLevel + 1)
////                    },
////                    1200
////                )
////            }
////
//            if (isCorrect) {
//                SoundManager.playCorrect()
//                optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_correct)
//                tvQuestion.postDelayed({
//                    val oldLevel = viewModel.currentLevel.value ?: 1
//                    viewModel.levelUpWithoutQuestion()
//                    showPrizeList(oldLevel, oldLevel + 1)
//                }, 1200)
//            }
//
//            else {
//                SoundManager.playWrong()
//                optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_wrong)
//                optionButtons[q.correctIndex].setBackgroundResource(R.drawable.bg_answer_correct)
//                tvQuestion.postDelayed({
//                    endGame(
//                        viewModel.wrongAnswer(), "Wrong answer"
//                    )
//                }, 1200)
//            }
//        }, 1200)
//    }
//
//
//    private fun resetOptionButtons() {
//        optionButtons.forEach { button ->
//            button.visibility = View.VISIBLE
//            button.isEnabled = true
//            button.alpha = 1f
//
//            button.setBackgroundResource(R.drawable.bg_answer_selector)
//            button.backgroundTintList = null
//
//            button.invalidate()
//            button.requestLayout()
//        }
//    }
//
//
//    private fun use5050() {
//        if (!viewModel.lifelinesUsed.add("50:50")) return
//        btn5050.isEnabled = false
//        btn5050.alpha = 0.5f
//
//        val q = viewModel.currentQuestion.value ?: return
//        val wrong = (0..3).filter { it != q.correctIndex }.shuffled()
//
//        optionButtons[wrong[0]].visibility = View.GONE
//        optionButtons[wrong[1]].visibility = View.GONE
//
//        SoundManager.playLifelineTimer(requireContext())
//    }
//
//
//    private fun usePhoneFriend() {
//        if (!viewModel.lifelinesUsed.add("PhoneFriend")) return
//
//        btnPhone.isEnabled = false
//        btnPhone.alpha = 0.5f
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_CONTACTS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            loadContacts()
//            showContactsDialog()
//        } else {
//            requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
//        }
//    }
//
//
//    private fun useGoogleSearch() {
//        if (!viewModel.lifelinesUsed.add("Google")) return
//        btnGoogle.isEnabled = false
//        btnGoogle.alpha = 0.5f
//
//        SoundManager.playLifelineTimer(requireContext())
//
//        val query = viewModel.currentQuestion.value?.text ?: return
//        startActivity(
//            Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("https://www.google.com/search?q=${query.replace(" ", "+")}")
//            )
//        )
//    }
//
//
//    private fun startLifelineTimer() {
//        isLifelineTimerRunning = true
//        tvTimer.visibility = View.VISIBLE
//        lifelineTimer?.cancel()
//        SoundManager.playLifelineTimer(requireContext())
//        lifelineTimer = object :
//            CountDownTimer(30_000, 1000) {
//            override fun onTick(ms: Long) {
//                tvTimer.text = "Time left: ${ms / 1000}s"
//            }
//
//            override fun onFinish() {
//                tvTimer.visibility = View.GONE
//                SoundManager.stopLifelineTimer()
//                endGame(viewModel.wrongAnswer(), "Time expired")
//            }
//        }.start()
//    }
//
//    private fun stopLifelineTimer() {
//        isLifelineTimerRunning = false
//        lifelineTimer?.cancel()
//        tvTimer.visibility = View.GONE
//        SoundManager.stopLifelineTimer()
//    }
//
//
//    private fun endGame(sum: Long, message: String) {
//        val level = viewModel.currentLevel.value ?: 1
//        val isWinner = sum == 1_000_000L
//
//        val intent = Intent(requireContext(), EndActivity::class.java).apply {
//            putExtra("WON_SUM", sum)
//            putExtra("MESSAGE", message)
//            putExtra("LEVEL", level)
//            putExtra("IS_WINNER", isWinner)
//        }
//
//        startActivity(intent)
//        requireActivity().finish()
//    }
//
//
//    private fun showPrizeList(oldLevel: Int, newLevel: Int) {
//        val fragment = PrizeListFragment.newInstance(oldLevel, newLevel)
//        parentFragmentManager.beginTransaction()
//            .setCustomAnimations(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                R.anim.slide_in_right,
//                R.anim.slide_out_left
//            )
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        if (viewModel.currentQuestion.value == null) {
//            val level = viewModel.currentLevel.value ?: 1
//            viewModel.fetchQuestion(level)
//        }
//    }
//
//    private fun Long.format(): String = "%,d".format(this)
//
//    override fun onDestroyView() {
//        lifelineTimer?.cancel()
//        super.onDestroyView()
//    }
//}


package com.example.millionaire_androidmobileapplication.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.millionaire_androidmobileapplication.R
import com.example.millionaire_androidmobileapplication.domain.model.quiz.QuizQuestion
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.Manifest
import android.content.ContentValues.TAG
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.millionaire_androidmobileapplication.EndActivity
import com.example.millionaire_androidmobileapplication.QuizViewModel
import com.example.millionaire_androidmobileapplication.domain.model.SoundManager
import com.google.android.material.button.MaterialButton


class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private val viewModel: QuizViewModel by activityViewModels()

    private lateinit var tvQuestion: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvWinnings: TextView
    private lateinit var tvTimer: TextView

    private lateinit var btnA: MaterialButton
    private lateinit var btnB: MaterialButton
    private lateinit var btnC: MaterialButton
    private lateinit var btnD: MaterialButton

    private lateinit var btn5050: ImageButton
    private lateinit var btnPhone: ImageButton
    private lateinit var btnGoogle: ImageButton
    private lateinit var btnQuit: ImageButton

    private lateinit var optionButtons: List<MaterialButton>

    private var lifelineTimer: CountDownTimer? = null
    private var isLifelineTimerRunning = false


    private val requestCallPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.selectedPhone?.let { phone ->
                    startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
                }
            }
        }

    private val requestContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                loadContacts()
                showContactsDialog()
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }

        bindViews(view)
        observeViewModel()
        setupListeners()
        restoreLifelineState()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.currentQuestion.value == null) {
            val level = viewModel.currentLevel.value ?: 1
            viewModel.fetchQuestion(level)
        }
    }

    override fun onDestroyView() {
        lifelineTimer?.cancel()
        super.onDestroyView()
    }


    private fun bindViews(view: View) {
        tvQuestion = view.findViewById(R.id.tv_question)
        tvLevel = view.findViewById(R.id.tv_level)
        tvWinnings = view.findViewById(R.id.tv_winnings)
        tvTimer = view.findViewById(R.id.tv_timer)

        btnA = view.findViewById(R.id.btn_option_a)
        btnB = view.findViewById(R.id.btn_option_b)
        btnC = view.findViewById(R.id.btn_option_c)
        btnD = view.findViewById(R.id.btn_option_d)

        btn5050 = view.findViewById(R.id.btn_5050)
        btnPhone = view.findViewById(R.id.btn_phone)
        btnGoogle = view.findViewById(R.id.btn_google)
        btnQuit = view.findViewById(R.id.btn_quit)

        optionButtons = listOf(btnA, btnB, btnC, btnD)
    }


    private fun observeViewModel() {
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            if (question != null) displayQuestion(question)
        }

        viewModel.currentLevel.observe(viewLifecycleOwner) { level ->
            tvLevel.text = "Level: $level / 15"
        }

        viewModel.winnings.observe(viewLifecycleOwner) { amount ->
            tvWinnings.text = "Winnings: $${amount.format()}"
        }

        viewModel.shouldLoadQuestion.observe(viewLifecycleOwner) { shouldLoad ->
            if (shouldLoad) {
                val level = viewModel.currentLevel.value ?: return@observe
                viewModel.fetchQuestion(level)
                viewModel.onQuestionLoaded()
            }
        }

        Log.d("QUIZ_FLOW", "QuizFragment ACTIVE, loading question")
    }


    private fun restoreLifelineState() {
        if ("50:50" in viewModel.lifelinesUsed) setLifelineDisabled(btn5050)
        if ("Google" in viewModel.lifelinesUsed) setLifelineDisabled(btnGoogle)
        if ("PhoneFriend" in viewModel.lifelinesUsed) setLifelineDisabled(btnPhone)
    }

    private fun setLifelineDisabled(btn: ImageButton) {
        btn.isEnabled = false
        btn.alpha = 0.5f
    }

    private fun displayQuestion(q: QuizQuestion) {
        resetOptionButtons()
        tvQuestion.text = q.text

        optionButtons.forEachIndexed { index, button ->
            button.text = "${'A' + index}: ${q.options[index]}"
            button.isEnabled = true
            button.visibility = View.VISIBLE
            button.setBackgroundResource(R.drawable.bg_answer_selector)
            button.backgroundTintList = null

            Log.d(TAG, "Setting button ${'A' + index} -> ${q.options[index]}")

            button.alpha = 0f
            button.scaleX = 0.8f
            button.scaleY = 0.8f
            button.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(1600).start()
        }

        if (!isLifelineTimerRunning) tvTimer.visibility = View.GONE
    }

    private fun resetOptionButtons() {
        optionButtons.forEach { button ->
            button.visibility = View.VISIBLE
            button.isEnabled = true
            button.alpha = 1f
            button.setBackgroundResource(R.drawable.bg_answer_selector)
            button.backgroundTintList = null
            button.invalidate()
            button.requestLayout()
        }
    }


    private fun setupListeners() {
        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener { checkAnswer(index) }
        }

        btn5050.setOnClickListener {
            use5050()
            startLifelineTimer()
            setLifelineDisabled(btn5050)
        }

        btnPhone.setOnClickListener {
            usePhoneFriend()
            setLifelineDisabled(btnPhone)
        }

        btnGoogle.setOnClickListener {
            useGoogleSearch()
            startLifelineTimer()
            setLifelineDisabled(btnGoogle)
        }

        btnQuit.setOnClickListener {
            btnQuit.isEnabled = false
            btnQuit.postDelayed({ endGame(viewModel.quitGame(), "You quit the game") }, 1200)
        }
    }


    private fun checkAnswer(selectedIndex: Int) {
        val q = viewModel.currentQuestion.value ?: return
        optionButtons.forEach { it.isEnabled = false }
        stopLifelineTimer()

        Log.d(TAG, "checkAnswer() called, options = ${q.options}")

        optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_pressed)
        val isCorrect = selectedIndex == q.correctIndex

        tvQuestion.postDelayed({
            if (isCorrect) {
                SoundManager.playCorrect()
                optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_correct)
                tvQuestion.postDelayed({
                    val oldLevel = viewModel.currentLevel.value ?: 1
                    viewModel.levelUpWithoutQuestion()
                    showPrizeList(oldLevel, oldLevel + 1)
                }, 1200)
            } else {
                SoundManager.playWrong()
                optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_answer_wrong)
                optionButtons[q.correctIndex].setBackgroundResource(R.drawable.bg_answer_correct)
                tvQuestion.postDelayed({
                    endGame(viewModel.wrongAnswer(), "Wrong answer")
                }, 1200)
            }
        }, 1200)
    }


    private fun use5050() {
        if (!viewModel.lifelinesUsed.add("50:50")) return
        val q = viewModel.currentQuestion.value ?: return
        val wrong = (0..3).filter { it != q.correctIndex }.shuffled()
        optionButtons[wrong[0]].visibility = View.GONE
        optionButtons[wrong[1]].visibility = View.GONE
        SoundManager.playLifelineTimer(requireContext())
    }

    private fun usePhoneFriend() {
        if (!viewModel.lifelinesUsed.add("PhoneFriend")) return
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
            showContactsDialog()
        } else {
            requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun useGoogleSearch() {
        if (!viewModel.lifelinesUsed.add("Google")) return
        SoundManager.playLifelineTimer(requireContext())
        val query = viewModel.currentQuestion.value?.text ?: return
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${query.replace(" ", "+")}")
            )
        )
    }


    private fun loadContacts() {
        val contacts = mutableListOf<String>()
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                contacts.add("${it.getString(nameIndex)}: ${it.getString(numberIndex)}")
            }
        }
        viewModel.phoneNumbers.clear()
        viewModel.phoneNumbers.addAll(contacts)
    }

    private fun showContactsDialog() {
        loadContacts()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Phone a Friend")
            .setItems(viewModel.phoneNumbers.toTypedArray()) { _, which ->
                val number = viewModel.phoneNumbers[which].substringAfter(": ").trim()
                startLifelineTimer()
                SoundManager.playLifelineTimer(requireContext())
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
            }
            .show()
    }


    private fun startLifelineTimer() {
        isLifelineTimerRunning = true
        tvTimer.visibility = View.VISIBLE
        lifelineTimer?.cancel()
        SoundManager.playLifelineTimer(requireContext())
        lifelineTimer = object : CountDownTimer(30_000, 1000) {
            override fun onTick(ms: Long) {
                tvTimer.text = "Time left: ${ms / 1000}s"
            }
            override fun onFinish() {
                tvTimer.visibility = View.GONE
                SoundManager.stopLifelineTimer()
                endGame(viewModel.wrongAnswer(), "Time expired")
            }
        }.start()
    }

    private fun stopLifelineTimer() {
        isLifelineTimerRunning = false
        lifelineTimer?.cancel()
        tvTimer.visibility = View.GONE
        SoundManager.stopLifelineTimer()
    }


    private fun endGame(sum: Long, message: String) {
        val level = viewModel.currentLevel.value ?: 1
        val isWinner = sum == 1_000_000L
        val intent = Intent(requireContext(), EndActivity::class.java).apply {
            putExtra("WON_SUM", sum)
            putExtra("MESSAGE", message)
            putExtra("LEVEL", level)
            putExtra("IS_WINNER", isWinner)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showPrizeList(oldLevel: Int, newLevel: Int) {
        val fragment = PrizeListFragment.newInstance(oldLevel, newLevel)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun Long.format(): String = "%,d".format(this)
}