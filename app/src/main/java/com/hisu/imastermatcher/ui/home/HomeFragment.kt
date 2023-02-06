package com.hisu.imastermatcher.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.databinding.FragmentHomeBinding
import com.hisu.imastermatcher.ui.dialog.DailyRewardDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGameMode()
        getDailyReward()
    }

    private fun initGameMode() = binding.apply {
        tvClassicMode.setOnClickListener { playClassicMode() }
        tvTimerMode.setOnClickListener { playTimerMode() }
    }

    private fun playClassicMode() {
        val action = HomeFragmentDirections.homeToMode(0)
        findNavController().navigate(action)
    }

    private fun playTimerMode() {
        val action = HomeFragmentDirections.homeToMode(1)
        findNavController().navigate(action)
    }

    private fun getDailyReward() = binding.btnDaily.setOnClickListener {
        val dailyRewardDialog = DailyRewardDialog(requireContext(), Gravity.CENTER)
        dailyRewardDialog.showDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}