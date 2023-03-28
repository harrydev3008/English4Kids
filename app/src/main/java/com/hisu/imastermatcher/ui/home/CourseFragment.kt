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
import com.hisu.imastermatcher.MainActivity
import com.hisu.imastermatcher.adapter.CourseItemViewPagerAdapter
import com.hisu.imastermatcher.databinding.FragmentCourseBinding
import com.hisu.imastermatcher.model.Course
import java.lang.Math.abs

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private lateinit var mainActivity: MainActivity
    private lateinit var courseAdapter: CourseItemViewPagerAdapter
    val binding get() = _binding!!

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

        initFeatureMovieList()

        binding.vpCourses.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                val curColor = pageColors[binding.vpCourses.currentItem]
                binding.parentContainer.setBackgroundColor(Color.parseColor(curColor))
                binding.vpCourses.setBackgroundColor(Color.parseColor(curColor))
                courseAdapter.changePage(position, curColor)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter { //course ->
            //todo: implement go to course here
            val action = CourseFragmentDirections.courseToLevel(mode = it.courseTitle)
            findNavController().navigate(action)
        }

        courseAdapter.courses = mutableListOf(
            Course(
                courseTitle = "Phần giới thiệu Tiếng Anh",
                courseDesc = "Khởi động cùng một số ngữ pháp và cụm từ đơn giản",
                currentLevel = 8,
                totalLevel = 8,
                isComplete = true
            ),
            Course(
                courseTitle = "Tiếng Anh Cơ Bản 1",
                courseDesc = "Học từ, cụm từ và chủ điểm ngữ pháp để giao tiếp cơ bản",
                currentLevel = 2,
                totalLevel = 20,
                isComplete = false
            ),
            Course(
                courseTitle = "Tiếng Anh Cơ Bản 2",
                courseDesc = "Học từ, cụm từ và chủ điểm ngữ pháp để giao tiếp nâng cao",
                currentLevel = 0,
                totalLevel = 20,
                isComplete = false
            ),
        )

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
        _binding = null
    }
}