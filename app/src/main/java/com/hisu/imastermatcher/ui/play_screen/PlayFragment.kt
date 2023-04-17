package com.hisu.imastermatcher.ui.play_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentPlayBinding
import com.hisu.imastermatcher.utils.MyUtils
import es.dmoral.toasty.Toasty

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private var startGamePlayTime: Long = 0

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

        quitGame()

        //todo: impl later
        calculatePlayTime()

        //Todo: update later
        tempQuestions = listOf<String>(
            "classic_pairs",
//            "word_pair",
            "audio_word_pair",
//            "audio_image_pair",
//            "sentence",
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

            val finishTime = System.currentTimeMillis()

            Toasty.error(requireContext(), "${finishTime}, ${startGamePlayTime}, ${(finishTime - startGamePlayTime) / 1000}s", Toasty.LENGTH_SHORT).show()

            val res = MyUtils.loadJsonFromAssets(requireActivity(), "result.json")
            val action = PlayFragmentDirections.gameFinish(res)
            findNavController().navigate(action)
        }
    }

    private fun handleWrongAnswer() {
        val currentLife = Integer.parseInt(binding.tvLife.text.toString())
        binding.tvLife.text = "${currentLife - 1}"
    }

    private fun quitGame() = binding.ibtnClose.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_dialog_msg))
            .setSubtitle(requireContext().getString(R.string.confirm_quit_game_play))
            .setBoldPositiveLabel(true)
            .setNegativeListener(requireContext().getString(R.string.confirm_msg_stay)){
                it.dismiss()
            }.setPositiveListener(requireContext().getString(R.string.confirm_msg_quit)) {
                it.dismiss()
                findNavController().popBackStack()
            }.build().show()
    }

    private fun calculatePlayTime() {
        startGamePlayTime = System.currentTimeMillis()
    }

    private fun calculateFinalResult() {
        //todo: impl later
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}