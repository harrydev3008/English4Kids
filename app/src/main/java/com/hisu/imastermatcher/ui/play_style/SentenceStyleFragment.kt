package com.hisu.imastermatcher.ui.play_style

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentSentenceStyleBinding
import com.hisu.imastermatcher.model.SentenceQuestion
import com.hisu.imastermatcher.widget.CustomWord
import com.hisu.imastermatcher.widget.MyCustomLayout
import java.util.*

class SentenceStyleFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : Fragment() {

    private var _binding: FragmentSentenceStyleBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private lateinit var questionModel: SentenceQuestion
    private lateinit var myCustomLayout: MyCustomLayout
    private lateinit var customWord: CustomWord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSentenceStyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCustomLayout()

        questionModel = SentenceQuestion(
            1, "Dịch câu này", "Bạn bao nhiêu tuổi?", "How old are you?",
            listOf(
                "She",
                "uncle",
                "him",
                "age",
                "hair",
                "your",
                "my"
            )
        )

        binding.tvModeLevel.text = questionModel.title
        binding.tvQuestion.text = questionModel.question
        initData()

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                wrongAnswerListener.invoke()
            }
        }

        addActionForBtnCheck()
    }

    private fun initCustomLayout() {
        myCustomLayout = MyCustomLayout(requireContext())
        myCustomLayout.setGravity(Gravity.CENTER)

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        binding.answerContainer.addView(myCustomLayout, params)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initData() {
        val wordsSentence = randomizeMoreWord().shuffled()

        for (word in wordsSentence) {
            val customWord = CustomWord(requireContext(), word)
            customWord.setOnTouchListener(TouchListener())
            myCustomLayout.push(customWord)
        }

        val layoutParams = binding.flAnswerContainer.layoutParams
        layoutParams.height =
            context?.resources?.getDimensionPixelSize(com.intuit.sdp.R.dimen._200sdp) ?: 200

        binding.flAnswerContainer.layoutParams = layoutParams
    }

    private fun randomizeMoreWord(): List<String> {
        val random = Random()
        val wordsFromSentence = questionModel.answer.split(" ");
        val words = mutableListOf<String>()
        words.addAll(wordsFromSentence)

        /**
         * count all words from the Answer and add 3 random words to confuse user
         * ex:
         *  + answer = "How old are you?"
         *  + randomWords = ["She",Uncle","Him","Age","Hair","Your","My"]
         *
         * we will get 3 more random words from the question.randomWords
         * out: [ "Him", "She", "How", "you?", "old", "are" ]
         */
        val totalWords = wordsFromSentence.size + 3

        while (words.size < totalWords) {
            val word = questionModel.randomWords[random.nextInt(questionModel.randomWords.size)]
            if (!words.contains(word))
                words.add(word)
        }

        return words
    }

    private fun lockViews() {
        for (i in 0 until binding.flAnswerContainer.childCount) {
            customWord = binding.flAnswerContainer.getChildAt(i) as CustomWord
            customWord.isEnabled = false
        }

        for (i in 0 until myCustomLayout.childCount) {
            customWord = myCustomLayout.getChildAt(i) as CustomWord
            customWord.isEnabled = false
        }
    }

    private fun checkChildCount() {
        if (binding.flAnswerContainer.childCount > 0) {
            binding.btnCheck.btnNextRound.isEnabled = true
            binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
            binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
            binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
        } else {
            binding.btnCheck.btnNextRound.isEnabled = false
            binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
            binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.gray_e5))
            binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.gray_af))
        }

        binding.btnCheck.containerWrong.visibility = View.GONE
        binding.btnCheck.tvCorrect.visibility = View.GONE
        binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.white))
    }

    private fun addActionForBtnCheck() = binding.btnCheck.btnNextRound.setOnClickListener {
        val answer: StringBuilder = StringBuilder()

        if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.check))) {
            for (i in 0 until binding.flAnswerContainer.childCount) {
                val curCustomWord = binding.flAnswerContainer.getChildAt(i) as CustomWord
                answer.append(curCustomWord.text.toString()).append(" ")
            }

            if (answer.toString().trim() == questionModel.answer) {
                lockViews()
                _result.postValue(true)
            } else {
                lockViews()
                _result.postValue(false)
                binding.btnCheck.tvCorrectAnswer.text = questionModel.answer
            }

        } else if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.next))) {
            itemTapListener.invoke()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class TouchListener : View.OnTouchListener {
        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            if (motionEvent?.action == MotionEvent.ACTION_DOWN && !myCustomLayout.empty()) {
                customWord = view as CustomWord
                customWord.goToGroupView(myCustomLayout, binding.flAnswerContainer)

                checkChildCount()
                return true
            }
            return false
        }
    }
}