package com.hisu.english4kids.ui.lessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentLessonsBinding
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.model.course.LessonsResponse

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

        backToHomePage()
        setUpLevels()
        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LessonAdapter(requireContext(), ::handleLessonClick)
        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun handleLessonClick(lesson: Lesson) {
        findNavController().navigate(R.id.action_classModeLevelFragment_to_gamePlayProgressFragment)
    }

    private fun loadLevel() {
        levelAdapter?.lessons = Gson().fromJson(myNavArgs.courseLevels, LessonsResponse::class.java)
    }
}