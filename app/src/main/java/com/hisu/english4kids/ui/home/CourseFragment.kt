package com.hisu.english4kids.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.adapter.CourseItemViewPagerAdapter
import com.hisu.english4kids.databinding.FragmentCourseBinding
import com.hisu.english4kids.model.course.CoursesResponse
import com.hisu.english4kids.utils.MyUtils
import kotlin.math.abs

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private lateinit var courseAdapter: CourseItemViewPagerAdapter
    val binding get() = _binding!!
    private lateinit var coursesResponse: CoursesResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCourses()
        initFeatureMovieList()
        backToHomePage()
    }

    private fun getCourses() {
        coursesResponse = Gson().fromJson(
            MyUtils.loadJsonFromAssets(requireActivity(), "courses.json"),
            CoursesResponse::class.java
        )
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter(requireContext()) {
            val action = CourseFragmentDirections.courseToLevel(
                mode = it.courseTitle,
                courseLevels = Gson().toJson(it.courseLevels)
            )
            findNavController().navigate(action)
        }

        courseAdapter.courses = coursesResponse

        val transformer = CompositePageTransformer()

        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 3

        adapter = courseAdapter

        setPageTransformer(transformer)
    }

    private fun backToHomePage() = binding.btnHomepage.setOnClickListener {
        findNavController().navigate(R.id.course_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}