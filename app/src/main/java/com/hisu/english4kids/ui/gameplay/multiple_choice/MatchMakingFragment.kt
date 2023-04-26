package com.hisu.english4kids.ui.gameplay.multiple_choice

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentMatchMakingBinding
import com.hisu.english4kids.ui.dialog.FindMatchDialog

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
        handleBackButton()
    }

    private fun handleFindMatchButton() = binding.btnFindMatch.setOnClickListener {
        val dialog = FindMatchDialog(requireContext(), Gravity.CENTER, ::handleAccept, ::handleCancel)
        dialog.showDialog()
    }

    private fun handleAccept() {
        findNavController().navigate(R.id.action_matchMakingFragment_to_multipleChoiceContainerFragment)
    }

    private fun handleCancel() {

    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.action_matchMakingFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}