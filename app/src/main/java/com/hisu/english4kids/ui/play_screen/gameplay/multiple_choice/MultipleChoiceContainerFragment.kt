package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.leader_board.User
import com.hisu.english4kids.databinding.FragmentMultipleChoiceContainerBinding
import com.hisu.english4kids.data.model.multiple_choice.MultipleChoiceModel
import com.hisu.english4kids.data.model.multiple_choice.MultipleChoicesResponse
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import java.util.concurrent.TimeUnit

class MultipleChoiceContainerFragment : Fragment() {

    private var _binding: FragmentMultipleChoiceContainerBinding? = null
    private val binding get() = _binding!!
    val questions = MultipleChoicesResponse()
    private var wrongAnswer = 0
    private var startGamePlayTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMultipleChoiceContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        handleNextQuestion()

        binding.apply {
            val localDataManager = LocalDataManager()
            localDataManager.init(requireContext())

            val user = Gson().fromJson(localDataManager.getUserInfo(), User::class.java)

            cardCompetitiveHeader.userFirst.tvUsername.text = user.username
        }
    }

    private fun setUpViewPager() = binding.vpMultipleQuestion.apply {
        val questionAdapter = MultipleChoiceViewPagerAdapter(
            requireActivity(), ::handleItemClick, ::handleWrongAnswer
        )

        startGamePlayTime = System.nanoTime()

        questions.add(
            MultipleChoiceModel(
                1,
                "Apple nghĩa là gì?",
                listOf("Táo", "Nho", "Dưa hấu", "Đào"),
                "Táo"
            )
        )
        questions.add(
            MultipleChoiceModel(
                2,
                "She ... to school by bus.",
                listOf("go", "went", "walk", "goes"),
                "goes"
            )
        )
        questions.add(
            MultipleChoiceModel(
                3,
                "Chọn câu dịch đúng: \"Tôi thích chó.\"",
                listOf("I love fish.", "I like cat.", "I hate dog.", "I love dog."),
                "I love dog."
            )
        )

        questionAdapter.questions = questions

        adapter = questionAdapter
        isUserInputEnabled = false
    }

    private fun handleNextQuestion() = binding.btnNextQuestion.setOnClickListener {
        if (binding.vpMultipleQuestion.currentItem < questions.size - 1)
            binding.vpMultipleQuestion.currentItem = binding.vpMultipleQuestion.currentItem + 1
    }

    private fun handleItemClick() {
        if (binding.vpMultipleQuestion.currentItem >= questions.size - 1) {
            val finishTime = System.nanoTime() - startGamePlayTime
            val minute = TimeUnit.NANOSECONDS.toMinutes(finishTime)
            val second = TimeUnit.NANOSECONDS.toSeconds(finishTime)

            val action = MultipleChoiceContainerFragmentDirections.actionMultipleChoiceContainerFragmentToCompleteCompetitiveFragment(
                result = "${questions.size - wrongAnswer}/${questions.size}",
                time = "$minute:${second}s"
            )
            findNavController().navigate(action)
        }
    }

    private fun handleWrongAnswer() {
        wrongAnswer++

        if (binding.vpMultipleQuestion.currentItem >= questions.size - 1) {
            val finishTime = System.nanoTime() - startGamePlayTime
            val minute = TimeUnit.NANOSECONDS.toMinutes(finishTime)
            val second = TimeUnit.NANOSECONDS.toSeconds(finishTime)

            val action = MultipleChoiceContainerFragmentDirections.actionMultipleChoiceContainerFragmentToCompleteCompetitiveFragment(
                result = "${questions.size - wrongAnswer}/${questions.size}",
                time = "$minute:${second}s"
            )
            findNavController().navigate(action)
        }
    }

    private var onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
//            binding.tvCurrentProgress.text = String.format(
//                requireContext().getString(R.string.question_progress_pattern),
//                position + 1,
//                questions.size
//            )
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding.vpMultipleQuestion.unregisterOnPageChangeCallback(onPageChangeCallback)
        _binding = null
    }
}