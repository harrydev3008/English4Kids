package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.result.FinalResult
import com.hisu.english4kids.databinding.FragmentCompleteCompetitiveBinding

class CompleteCompetitiveFragment : Fragment() {

    private var _binding: FragmentCompleteCompetitiveBinding?= null
    private val binding get() = _binding!!
    private val myNavArgs: CompleteCompetitiveFragmentArgs by navArgs()

    private lateinit var result: FinalResult

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCompleteCompetitiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        result = Gson().fromJson(myNavArgs.result, FinalResult::class.java)

        renderResult()
        handleConfirmButton()
        handleReviewAnswer()
    }

    private fun renderResult() =  binding.apply {
        tvCorrectAnswer.text = "Câu đúng\n${result.correctCount}"
        tvCompeleteTime.text = "Thời gian\n${result.fastScore}"
        tvGold.text = String.format(requireContext().getString(R.string.bonus_gold_pattern), result.golds)
        tvWeeklyScore.text = String.format(requireContext().getString(R.string.bonus_gold_pattern), result.totalScore)
    }

    private fun handleConfirmButton() = binding.btnFinish.setOnClickListener {
        findNavController().navigate(R.id.action_completeCompetitiveFragment_to_homeFragment)
    }

    private fun handleReviewAnswer() = binding.btnReview.setOnClickListener {
        val action = CompleteCompetitiveFragmentDirections.actionCompleteCompetitiveFragmentToReviewAnswerFragment(questions = myNavArgs.answers)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}