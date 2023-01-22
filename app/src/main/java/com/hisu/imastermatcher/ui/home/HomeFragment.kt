package com.hisu.imastermatcher.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?= null
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}