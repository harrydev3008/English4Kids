package com.hisu.english4kids.ui.play_screen.gameplay.audio_word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.GameStyleFive
import com.hisu.english4kids.databinding.FragmentMatchingAudioWordBinding
import com.hisu.english4kids.utils.MyUtils
import java.util.regex.Pattern

class MatchingAudioWordFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: (roundId: String, position: Int, playStatus: String) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String, playStatus: String) -> Unit,
    private val gameStyleFive: GameStyleFive,
    private var gameIndex: Int
) : Fragment() {

    private var _binding: FragmentMatchingAudioWordBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result
    private lateinit var answer: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingAudioWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioImageAdapter = MatchingAudioWordAdapter(requireContext()) {
            answer = it.cardId

            if(!MyUtils.isVietnameseWord(it.word))
                (requireActivity() as MainActivity).speakText(it.word)

            if (!binding.btnCheck.btnNextRound.isEnabled) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        audioImageAdapter.pairs = gameStyleFive.cards

        binding.rvPickAnswer.adapter = audioImageAdapter

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))

                audioImageAdapter.isLockView = true
                correctAnswerListener.invoke(gameStyleFive.score, gameStyleFive.roundId, gameStyleFive.playStatus?:"NONE")
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                audioImageAdapter.isLockView = true

                wrongAnswerListener.invoke(gameStyleFive.roundId, gameIndex, gameStyleFive.playStatus?:"NONE")
            }
        }

        checkAnswer()
        playAudio()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text == requireContext().getString(R.string.check)) {
            if (answer == gameStyleFive.correctAns) {
                _result.postValue(true)
            } else {
                _result.postValue(false)

                val correctAnswer = gameStyleFive.cards.first {
                    it.cardId == gameStyleFive.correctAns
                }

                binding.btnCheck.tvCorrectAnswer.text = correctAnswer.word
            }
        } else {
            itemTapListener.invoke()
        }
    }

    private fun playAudio() = binding.ibtnAudioQuestion.setOnClickListener {
        (requireActivity() as MainActivity).playAudio(gameStyleFive.question)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}