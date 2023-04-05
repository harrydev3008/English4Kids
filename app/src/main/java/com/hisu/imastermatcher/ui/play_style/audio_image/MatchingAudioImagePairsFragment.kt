package com.hisu.imastermatcher.ui.play_style.audio_image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentMatchingAudioImagePairsBinding
import com.hisu.imastermatcher.model.pair_matching.PairMatchingModel
import com.hisu.imastermatcher.model.pair_matching.PairMatchingResponse

class MatchingAudioImagePairsFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : Fragment() {

    private var _binding: FragmentMatchingAudioImagePairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var audioImageResponse: PairMatchingResponse
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result
    private lateinit var answer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchingAudioImagePairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioImageResponse = PairMatchingResponse(
            listOf(
                PairMatchingModel(1,1, R.drawable.grampa, "Ông"),
                PairMatchingModel(2,2, R.drawable.family, "Gia đình"),
                PairMatchingModel(3,3, R.drawable.husband, "Chồng"),
                PairMatchingModel(4,4, R.drawable.img_test_1, "Dê dừa"),
            ),
            "Coco-goat",
            "Dê dừa"
        )

        binding.tvQuestion.text = audioImageResponse.question

        val audioImageAdapter = MatchingAudioImageAdapter(requireContext()) {
            answer = it.answer

            if (!binding.btnCheck.btnNextRound.isEnabled) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        audioImageAdapter.pairs = audioImageResponse.images

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
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                audioImageAdapter.isLockView = true

                wrongAnswerListener.invoke()
            }
        }

        checkAnswer()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text == requireContext().getString(R.string.check)) {
            if (answer == audioImageResponse.correctAnswer) {
                _result.postValue(true)
            } else {
                _result.postValue(false)
                binding.btnCheck.tvCorrectAnswer.text = audioImageResponse.correctAnswer
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