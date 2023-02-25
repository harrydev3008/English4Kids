package com.hisu.imastermatcher.ui.finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentGameFinishBinding
import com.hisu.imastermatcher.ui.play_screen.PlayFragmentArgs

class GameFinishFragment : Fragment() {

    private var _binding: FragmentGameFinishBinding?= null
    private val binding get() = _binding!!
    private val myNavArgs: GameFinishFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playHolder = "Matched all pairs with ${myNavArgs.moves} moves!"
        binding.tvMoves.text = playHolder

        replay()
        next()
        back()
    }

    private fun replay() = binding.btnReplay.setOnClickListener {
        findNavController().navigate(R.id.replay)
    }

    private fun next() = binding.btnNextLevel.setOnClickListener {
        //todo: method to nagivate to next level
    }

    private fun back() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.back_to_level)
    }
}