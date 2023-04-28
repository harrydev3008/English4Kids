package com.hisu.english4kids.ui.finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentGameFinishBinding
import com.hisu.english4kids.data.model.result.FinalResult

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
    }

    private fun displayUserResult(result: FinalResult) = binding.apply {
        tvFastScore.text = result.fastScore
        tvPerfectScore.text = String.format(requireContext().getString(R.string.perfect_score_pattern), result.perfectScore)
        tvTotalExpScore.text = result.totalScore.toString()
    }

    private fun nextRound() = binding.btnNextLevel.setOnClickListener {
        //todo: impl method to go to next round
    }
}