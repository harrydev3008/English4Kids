package com.hisu.english4kids.ui.play_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.R
import com.hisu.english4kids.data.BUNDLE_LESSON_DATA
import com.hisu.english4kids.data.model.game_play.GameStyleOne
import com.hisu.english4kids.data.model.result.FinalResult
import com.hisu.english4kids.databinding.FragmentPlayBinding
import com.hisu.english4kids.widget.dialog.GameFinishDialog
import com.hisu.english4kids.widget.dialog.PurchaseHeartDialog
import java.util.concurrent.TimeUnit

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private var startGamePlayTime: Long = 0
    private var wrongAnswer = 0

    private lateinit var gameplayViewPagerAdapter: GameplayViewPagerAdapter
    private lateinit var gameplays: List<Object>
    private lateinit var heartDialog: PurchaseHeartDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()
        handleQuitGameButton()
        startGamePlayTime = System.nanoTime()

        val itemType = object : TypeToken<List<Object>>() {}.type
        val temp = Gson().fromJson<List<Object>>(arguments?.getString(BUNDLE_LESSON_DATA), itemType)
        gameplays = temp

        binding.pbStar.max = gameplays.size

        setUpViewpager()
    }

    private fun setUpViewpager() = binding.flRoundContainer.apply {
        isUserInputEnabled = false

        gameplayViewPagerAdapter =
            GameplayViewPagerAdapter(requireActivity(), ::handleNextQuestion, ::handleWrongAnswer)
        gameplayViewPagerAdapter.setGamePlays(gameplays)

        adapter = gameplayViewPagerAdapter
    }

    private fun handleNextQuestion() {

        if (Integer.parseInt(binding.tvLife.text.toString()) < 1) {
            heartDialog.showDialog()
            return
        }

        if (binding.flRoundContainer.currentItem < gameplays.size - 1) {
            binding.pbStar.progress = binding.pbStar.progress + 1
            binding.flRoundContainer
                .setCurrentItem(binding.flRoundContainer.currentItem + 1, true)
        } else {
            val finishTime = System.nanoTime() - startGamePlayTime

            val finishDialog = GameFinishDialog(requireContext(), calculateFinalResult(finishTime))
            finishDialog.showDialog()

            finishDialog.setExitCallback {
                finishDialog.dismissDialog()
                findNavController().navigate(R.id.action_playFragment_to_courseFragment)
            }

            finishDialog.setNextLessonCallback {

                wrongAnswer = 0
                binding.pbStar.max = gameplays.size
                gameplayViewPagerAdapter.setGamePlays(gameplays)
                binding.flRoundContainer.adapter = gameplayViewPagerAdapter

                finishDialog.dismissDialog()
            }
        }
    }

    private fun handleWrongAnswer() {
        var currentLife = Integer.parseInt(binding.tvLife.text.toString())
        currentLife--
        wrongAnswer++
        binding.tvLife.text = "$currentLife"

        if (currentLife == 0)
            heartDialog.showDialog()
    }

    private fun initDialog() {
        heartDialog = PurchaseHeartDialog(requireContext())

        heartDialog.setPurchaseCallback {
            if (it == 500) {// full purchase
                binding.tvLife.text = "5"
            } else if (it == 300) {//purchase 3
                binding.tvLife.text = "3"
            }

            //todo: calculate total coin left after purchasing heart
        }
    }

    private fun handleQuitGameButton() = binding.ibtnClose.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_dialog_msg))
            .setSubtitle(requireContext().getString(R.string.confirm_quit_game_play))
            .setBoldPositiveLabel(true)
            .setNegativeListener(requireContext().getString(R.string.confirm_msg_stay)) {
                it.dismiss()
            }.setPositiveListener(requireContext().getString(R.string.confirm_msg_quit)) {
                it.dismiss()
                findNavController().navigate(R.id.action_playFragment_to_courseFragment)
            }.build().show()
    }

    private fun calculateFinalResult(finishTime: Long): JsonElement {

        val minute = TimeUnit.NANOSECONDS.toMinutes(finishTime)
        val second = TimeUnit.NANOSECONDS.toSeconds(finishTime)

        //todo: update calculate score later
        val finalResult = FinalResult(
            "$minute:$second",
            (((gameplays.size - wrongAnswer).toFloat() / gameplays.size) * 100).toInt(),
            (gameplays.size - wrongAnswer) * if (minute < 1) 2 else 1
        )

        return Gson().toJsonTree(finalResult)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}