package com.hisu.imastermatcher.ui.home

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentHomeBinding
import com.hisu.imastermatcher.ui.dialog.DailyRewardDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        learningMode()
        competitiveMode()
        dailyReward()

        binding.btnCompetitiveMode.isEnabled = true
    }

    private fun learningMode() = binding.btnLearningMode.setOnClickListener {
        findNavController().navigate(R.id.home_to_course)
    }

    private fun competitiveMode() = binding.btnCompetitiveMode.setOnClickListener {
        findNavController().navigate(R.id.home_to_competitive)
    }

    private fun dailyReward() = binding.btnDailyReward.setOnClickListener {
        val dialog = DailyRewardDialog(requireContext(), Gravity.CENTER)
        dialog.showDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}