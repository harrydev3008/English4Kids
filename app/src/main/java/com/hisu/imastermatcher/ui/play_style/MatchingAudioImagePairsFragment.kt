package com.hisu.imastermatcher.ui.play_style

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentMatchingAudioImagePairsBinding
import com.hisu.imastermatcher.databinding.FragmentSentenceStyleBinding
import com.hisu.imastermatcher.model.AudioImageMatchingModel
import com.hisu.imastermatcher.model.AudioImageMatchingResponse

class MatchingAudioImagePairsFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : Fragment() {

    private var _binding: FragmentMatchingAudioImagePairsBinding?= null
    private val binding get() = _binding!!

    private lateinit var audioImageResponse: AudioImageMatchingResponse
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

        audioImageResponse = AudioImageMatchingResponse(
            listOf(
                AudioImageMatchingModel(R.drawable.grampa, "Ông"),
                AudioImageMatchingModel(R.drawable.family, "Gia đình"),
                AudioImageMatchingModel(R.drawable.husband, "Chồng"),
                AudioImageMatchingModel(R.drawable.img_test_1, "Dê dừa"),
            ),
            "Coco-goat",
            "Dê dừa"
        )

        binding.tvModeLevel.text = "Chọn hình ảnh đúng"
        binding.tvQuestion.text = audioImageResponse.question

        val audioImageAdapter = MatchingAudioImageAdapter(requireContext()) {
            answer = it.answer
        }

        audioImageAdapter.images = audioImageResponse.images

        binding.rvPickAnswer.adapter = audioImageAdapter

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

        binding.btnCheck.btnNextRound.isEnabled = true
        checkAnswer()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {


        if(answer == audioImageResponse.correctAnswer) {
            _result.postValue(true)
        } else {
            _result.postValue(false)
            binding.btnCheck.tvCorrectAnswer.text = audioImageResponse.correctAnswer
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}