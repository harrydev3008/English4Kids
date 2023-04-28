package com.hisu.english4kids.ui.play_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentPlayBinding
import com.hisu.english4kids.data.model.result.FinalResult
import java.util.concurrent.TimeUnit

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private var startGamePlayTime: Long = 0
    private var wrongAnswer = 0

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

        handleQuitGameButton()
        startGamePlayTime = System.nanoTime()

        //Todo: update later
        tempQuestions = listOf<String>(
//            "classic_pairs",
            "word_pair",
            "audio_word_pair",
            "audio_image_pair",
//            "sentence",
            "lol"
        )

        binding.pbStar.max = tempQuestions.size

        setUpViewpager()
    }

    private fun setUpViewpager() = binding.flRoundContainer.apply {
        isUserInputEnabled = false

        val temp = GameplayViewPagerAdapter(requireActivity(), ::handleNextQuestion, ::handleWrongAnswer)
        temp.gameplays = tempQuestions

        adapter = temp
    }

    private fun handleNextQuestion() {
        if (binding.flRoundContainer.currentItem < tempQuestions.size - 1) {
            binding.pbStar.progress = binding.pbStar.progress + 1
            binding.flRoundContainer
                .setCurrentItem(binding.flRoundContainer.currentItem + 1, true)
        } else {
            val finishTime = System.nanoTime() - startGamePlayTime
            val action = PlayFragmentDirections.gameFinish(Gson().toJson(calculateFinalResult(finishTime)))
            findNavController().navigate(action)
        }
    }

    private fun handleWrongAnswer() {
        val currentLife = Integer.parseInt(binding.tvLife.text.toString())
        binding.tvLife.text = "${currentLife - 1}"
        wrongAnswer++
    }

    private fun handleQuitGameButton() = binding.ibtnClose.setOnClickListener {
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

    private fun calculateFinalResult(finishTime: Long): String {

        val minute = TimeUnit.NANOSECONDS.toMinutes(finishTime)
        val second = TimeUnit.NANOSECONDS.toSeconds(finishTime)

        //todo: update calculate score later
        val finalResult =  FinalResult(
            "$minute:$second",
            (((tempQuestions.size - wrongAnswer).toFloat() / tempQuestions.size) * 100).toInt() ,
            (tempQuestions.size - wrongAnswer) * if(minute < 1) 2 else 1)

        return Gson().toJson(finalResult)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}