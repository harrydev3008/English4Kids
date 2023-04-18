package com.hisu.imastermatcher.ui.gameplay.multiple_choice

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentMatchMakingBinding
import com.hisu.imastermatcher.ui.dialog.FindMatchDialog

class MatchMakingFragment : Fragment() {

    private var _binding: FragmentMatchMakingBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchMakingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleFindMatchButton()
    }

    private fun handleFindMatchButton() = binding.btnFindMatch.setOnClickListener {
        val dialog = FindMatchDialog(requireContext(), Gravity.CENTER)
        dialog.showDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}