package com.hisu.imastermatcher.ui.lessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentLessonsBinding
import com.hisu.imastermatcher.model.course.LessonsResponse

class ClassModeLevelFragment : Fragment() {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: ClassModeLevelFragmentArgs by navArgs()
    private var levelAdapter: LessonAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mode = myNavArgs.mode
        binding.tvMode.text = mode

//        continueLevel()
        backToHomePage()
        setUpLevels()
        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        findNavController().navigate(R.id.lesson_to_home)
    }

//    private fun continueLevel() = binding.btnContinue.setOnClickListener {
//        findNavController().navigate(R.id.mode_to_play)
//    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LessonAdapter(requireContext()) {
            findNavController().navigate(R.id.mode_to_play)
        }

        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun loadLevel() {
        levelAdapter?.lessons = Gson().fromJson(myNavArgs.courseLevels, LessonsResponse::class.java)
    }
}