package com.hisu.imastermatcher.ui.mode_level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentModeLevelBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.model.CourseLevel


class ClassModeLevelFragment : Fragment() {

    private var _binding: FragmentModeLevelBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: ClassModeLevelFragmentArgs by navArgs()
    private var levelAdapter: LevelAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mode = myNavArgs.mode

        binding.tvMode.text = mode

        continueLevel()
        backToHomePage()
        setUpLevels()

        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        activity?.onBackPressed()
    }

    private fun continueLevel() = binding.btnContinue.setOnClickListener {
//        val action = ClassModeLevelFragmentDirections.modeToPlay(myNavArgs.mode)
//        findNavController().navigate(action)
        findNavController().navigate(R.id.mode_to_play)
    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LevelAdapter() {


            val cards = mutableListOf<Card>()

//            val action = ClassModeLevelFragmentDirections.modeToPlay(mode = myNavArgs.mode, level = it.id)
//            findNavController().navigate(action)
            findNavController().navigate(R.id.mode_to_play)
        }

        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun loadLevel() {
        val levels = listOf<CourseLevel>(
            CourseLevel(1,  1,"Đưa ra yêu cầu lịch sự, miêu tả vị trí đồ đạc", 3f),
            CourseLevel(2,  1, "Học từ, cụm từ và chủ điểm ngữ pháp để giao tiếp nâng cao",2.5f),
            CourseLevel(4,  0, "Khởi động cùng một số ngữ pháp và cụm từ đơn giản",0f),
            CourseLevel(5,  -1, "vl luon dau cut moiz",0f)
        )

        levelAdapter?.items = levels
    }
}