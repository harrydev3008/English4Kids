package com.hisu.imastermatcher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
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
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter { //course ->
            //todo: implement go to course here
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