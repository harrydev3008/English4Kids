package com.hisu.imastermatcher.ui.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hisu.imastermatcher.databinding.FragmentMultipleChoiceBinding
import com.hisu.imastermatcher.model.multiple_choice.MultipleChoiceModel

class MultipleChoiceFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit,
    var question: MultipleChoiceModel
) : Fragment() {

    private var _binding: FragmentMultipleChoiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMultipleChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestion.text = question.question

        binding.rvChoices.apply {
            val questionAdapter = MultipleChoiceAdapter(requireContext(), ::handleItemClick)
            questionAdapter.choices = question.choices

            adapter = questionAdapter
        }
    }

    private fun handleItemClick(model: String, position: Int) {
        if(model === question.correctAnswer) {
            itemTapListener.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}