package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.data.model.exam.ExamQuestion
import com.hisu.english4kids.databinding.FragmentReviewAnswerBinding

class ReviewAnswerFragment : Fragment() {

    private var _binding: FragmentReviewAnswerBinding?= null
    private val binding get() = _binding!!

    private val myNavArgs: ReviewAnswerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReviewAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackButton()
        setUpReviewRecyclerView()
    }

    private fun setUpReviewRecyclerView() = binding.rvAnswers.apply {
       val reviewAdapter = ReviewAnswerAdapter(requireContext()) {
           val action = ReviewAnswerFragmentDirections.actionReviewAnswerFragmentToReviewQuestionFragment(Gson().toJson(it))
            findNavController().navigate(action)
        }

        val itemType = object : TypeToken<List<ExamQuestion>>() {}.type
        val tempQuestions = Gson().fromJson<List<ExamQuestion>>(myNavArgs.questions, itemType)
        reviewAdapter.questions = tempQuestions
        adapter = reviewAdapter
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}