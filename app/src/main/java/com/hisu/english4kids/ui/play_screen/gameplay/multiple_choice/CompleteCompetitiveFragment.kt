package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentCompleteCompetitiveBinding
import com.hisu.english4kids.ui.lessons.ClassModeLevelFragmentArgs

class CompleteCompetitiveFragment : Fragment() {

    private var _binding: FragmentCompleteCompetitiveBinding?= null
    private val binding get() = _binding!!
    private val myNavArgs: CompleteCompetitiveFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompleteCompetitiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvCorrectAnswer.text = "Câu đúng\n${myNavArgs.result}"
            tvCompeleteTime.text = "Thời gian\n${myNavArgs.time}"
        }

        handleConfirmButton()
    }

    private fun handleConfirmButton() = binding.btnFinish.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_dialog_msg))
            .setSubtitle(requireContext().getString(R.string.competitive_complete_desc))
            .setBoldPositiveLabel(true)
            .setNegativeListener(requireContext().getString(R.string.find_next_opponent)){
                it.dismiss()
                findNavController().navigate(R.id.action_completeCompetitiveFragment_to_matchMakingFragment)
            }.setPositiveListener(requireContext().getString(R.string.cancel_match)) {
                it.dismiss()
                findNavController().navigate(R.id.action_completeCompetitiveFragment_to_homeFragment)
            }.build().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}