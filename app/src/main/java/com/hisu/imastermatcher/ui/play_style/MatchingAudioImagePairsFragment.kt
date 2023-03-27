package com.hisu.imastermatcher.ui.play_style

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentMatchingAudioImagePairsBinding
import com.hisu.imastermatcher.databinding.FragmentSentenceStyleBinding

class MatchingAudioImagePairsFragment(private val itemTapListener: () -> Unit) : Fragment() {

    private var _binding: FragmentMatchingAudioImagePairsBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchingAudioImagePairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCheck.btnNextRound.isEnabled = true
        binding.btnCheck.btnNextRound.setOnClickListener {
            itemTapListener.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}