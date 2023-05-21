package com.hisu.english4kids.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding?= null
    private val binding get() = _binding!!
    private var mode:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleStarterModeClick()
        handleIntermediateModeClick()
        handleSaveButtonEvent()
    }

    private fun handleSaveButtonEvent() = binding.btnSave.setOnClickListener {
        findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
    }

    private fun handleStarterModeClick() = binding.layoutStarter.setOnClickListener {
        mode = 1

        binding.layoutStarter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_green)
        binding.layoutInter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border)
    }

    private fun handleIntermediateModeClick() = binding.layoutInter.setOnClickListener {
        mode = 2

        binding.layoutInter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_green)
        binding.layoutStarter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}