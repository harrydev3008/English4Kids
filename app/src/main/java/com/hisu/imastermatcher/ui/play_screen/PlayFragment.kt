package com.hisu.imastermatcher.ui.play_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.databinding.FragmentPlayBinding
import org.json.JSONObject

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var tempQuestions: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initRecyclerView()
//
//        val lvl = myNavArgs.level
//        val mode = myNavArgs.mode

//        if (mode == 0)
//            levelPlaceHolder = "${getString(R.string.mode_classic)} ${lvl}"
//        else if (mode == 1)
//            levelPlaceHolder = "${getString(R.string.mode_timer)} ${lvl}"

        pauseGame()

        //Todo: update later
        tempQuestions = listOf<String>(
            "classic_pairs",
            "audio_image_pair",
            "word_pair",
            "sentence",
            "lol"
        )

        binding.pbStar.max = tempQuestions.size

        binding.flRoundContainer.isUserInputEnabled = false
        val temp =
            GameplayViewPagerAdapter(requireActivity(), ::handleNextQuestion, ::handleWrongAnswer)

        temp.gameplays = tempQuestions
        binding.flRoundContainer.adapter = temp
    }

    private fun handleNextQuestion() {
        if (binding.flRoundContainer.currentItem < tempQuestions.size - 1) {
            binding.pbStar.progress = binding.pbStar.progress + 1
            binding.flRoundContainer
                .setCurrentItem(binding.flRoundContainer.currentItem + 1, true)
        } else {
            //TODO: use some 'method' to calculate total score later
            val res = JSONObject()
            res.put("fast_score", "3:33")
            res.put("perfect_score", "99")
            res.put("total_score", "69")
            val action = PlayFragmentDirections.gameFinish(res.toString())
            findNavController().navigate(action)
        }
    }

    private fun handleWrongAnswer() {
        val currentLife = Integer.parseInt(binding.tvLife.text.toString())
        binding.tvLife.text = "${currentLife - 1}"
    }

    private fun pauseGame() = binding.ibtnClose.setOnClickListener {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}