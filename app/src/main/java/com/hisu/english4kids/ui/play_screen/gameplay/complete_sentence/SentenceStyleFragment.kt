package com.hisu.english4kids.ui.play_screen.gameplay.complete_sentence

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.GameStyleThree
import com.hisu.english4kids.databinding.FragmentSentenceStyleBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.widget.CustomWord
import com.hisu.english4kids.widget.MyCustomLayout
import java.util.*

class SentenceStyleFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: (roundId: String, position: Int, playStatus: String) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String, playStatus: String) -> Unit,
    private val gameStyleThree: GameStyleThree,
    private var gameIndex: Int
) : Fragment() {

    private var _binding: FragmentSentenceStyleBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private lateinit var myCustomLayout: MyCustomLayout
    private lateinit var customWord: CustomWord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSentenceStyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCustomLayout()

        binding.tvQuestion.text = gameStyleThree.question
        initData()

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                correctAnswerListener.invoke(gameStyleThree.score, gameStyleThree.roundId, gameStyleThree.playStatus?:"NONE")
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                wrongAnswerListener.invoke(gameStyleThree.roundId, gameIndex, gameStyleThree.playStatus?:"NONE")
            }
        }

        addActionForBtnCheck()
        playAudioQuestion()
    }

    private fun playAudioQuestion() = binding.btnPlayAudio.setOnClickListener {
        if(!MyUtils.isVietnameseWord(gameStyleThree.question))
            (requireActivity() as MainActivity).speakText(gameStyleThree.question)
    }

    private fun initCustomLayout() {
        myCustomLayout = MyCustomLayout(requireContext())
        myCustomLayout.setGravity(Gravity.CENTER)

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            requireContext().resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp),
            requireContext().resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
            0,
            0
        )

        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        binding.answerContainer.addView(myCustomLayout, params)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initData() {
        val wordsSentence = gameStyleThree.randomWords

        for (word in wordsSentence) {
            val customWord = CustomWord(requireContext(), word)
            customWord.setOnTouchListener(TouchListener())
            myCustomLayout.push(customWord)
        }
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

            if (answer.toString().trim() == gameStyleThree.correctAns) {
                lockViews()
                _result.postValue(true)
            } else {
                lockViews()
                _result.postValue(false)
                binding.btnCheck.tvCorrectAnswer.text = gameStyleThree.correctAns
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