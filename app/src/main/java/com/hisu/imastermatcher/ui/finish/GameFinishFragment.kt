package com.hisu.imastermatcher.ui.finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentGameFinishBinding
import com.hisu.imastermatcher.model.FinalResult
import com.hisu.imastermatcher.ui.play_screen.PlayFragmentArgs
import org.json.JSONObject

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

        val result = Gson().fromJson(myNavArgs.result, FinalResult::class.java)
        displayUserResult(result)

        nextRound()
        backToCourse()
    }

    private fun displayUserResult(result: FinalResult) = binding.apply {
        tvFastScore.text = result.fastScore
        tvPerfectScore.text = String.format(requireContext().getString(R.string.perfect_score_pattern), result.perfectScore)
        tvTotalExpScore.text = result.totalScore.toString()
    }

    private fun nextRound() = binding.btnNextLevel.setOnClickListener {
        //todo: impl method to go to next round
        findNavController().navigate(R.id.to_leader_board)
    }

    private fun backToCourse() = binding.ibtnClose.setOnClickListener {
        findNavController().navigate(R.id.back_to_level)
    }
}