package com.hisu.imastermatcher.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import com.hisu.imastermatcher.MainActivity
import com.hisu.imastermatcher.adapter.CourseItemViewPagerAdapter
import com.hisu.imastermatcher.databinding.FragmentCourseBinding
import com.hisu.imastermatcher.model.course.CoursesResponse
import com.hisu.imastermatcher.utils.MyUtils
import kotlin.math.abs


class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private lateinit var courseAdapter: CourseItemViewPagerAdapter
    val binding get() = _binding!!
    private lateinit var coursesResponse: CoursesResponse

    private val pageColors = listOf<String>(
        "#02AF84", "#FE63BD", "#20B0F5"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo: call api to fetch courses data or get data from local room db
        getCourses()
        initFeatureMovieList()

        binding.vpCourses.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun getCourses() {
        coursesResponse = Gson().fromJson(MyUtils.loadJsonFromAssets(requireActivity(), "courses.json"), CoursesResponse::class.java)
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter {
            val action = CourseFragmentDirections.courseToLevel(mode = it.courseTitle, courseLevels = Gson().toJson(it.courseLevels))
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpCourses.unregisterOnPageChangeCallback(pageChangeCallback)
        _binding = null
    }

    private val pageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            val curColor = pageColors[binding.vpCourses.currentItem]
            binding.parentContainer.setBackgroundColor(Color.parseColor(curColor))
            binding.vpCourses.setBackgroundColor(Color.parseColor(curColor))
            courseAdapter.changePage(position, curColor)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}