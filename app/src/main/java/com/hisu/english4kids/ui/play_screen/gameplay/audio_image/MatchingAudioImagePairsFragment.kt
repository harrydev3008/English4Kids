package com.hisu.english4kids.ui.play_screen.gameplay.audio_image

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.GameStyleTwo
import com.hisu.english4kids.databinding.FragmentMatchingAudioImagePairsBinding
import com.hisu.english4kids.data.model.pair_matching.PairMatchingModel
import com.hisu.english4kids.data.model.pair_matching.PairMatchingResponse

class MatchingAudioImagePairsFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: (position: Int, isPlayed: Boolean) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String, isPlayed: Boolean) -> Unit,
    private val gameStyleTwo: GameStyleTwo,
    private var gameIndex: Int
) : Fragment() {

    private var _binding: FragmentMatchingAudioImagePairsBinding? = null
    private val binding get() = _binding!!

    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result
    private lateinit var answer: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingAudioImagePairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestion.text = gameStyleTwo.question

        val audioImageAdapter = MatchingAudioImageAdapter(requireContext()) {
            answer = it.cardId

            if (!binding.btnCheck.btnNextRound.isEnabled) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        audioImageAdapter.pairs = gameStyleTwo.cards

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
                correctAnswerListener.invoke(gameStyleTwo.score, gameStyleTwo.roundId, gameStyleTwo.isPlayed)
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                audioImageAdapter.isLockView = true

                wrongAnswerListener.invoke(gameIndex, gameStyleTwo.isPlayed)
            }
        }

        checkAnswer()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text == requireContext().getString(R.string.check)) {
            if (answer == gameStyleTwo.correctAns) {
                _result.postValue(true)
            } else {
                _result.postValue(false)

                val correctAns = gameStyleTwo.cards.first {
                    it.cardId == gameStyleTwo.correctAns
                }

                binding.btnCheck.tvCorrectAnswer.text = correctAns.word
            }
        } else {
            itemTapListener.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}