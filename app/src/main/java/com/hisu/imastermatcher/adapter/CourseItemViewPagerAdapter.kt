package com.hisu.imastermatcher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.databinding.LayoutCourseItemBinding
import com.hisu.imastermatcher.model.Course

class CourseItemViewPagerAdapter(
    private val itemClickListener: (course: Course) -> Unit
): RecyclerView.Adapter<CourseItemViewPagerAdapter.CourseItemViewHolder>() {

    var courses: List<Course> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        val course = courses[position]
        bindData(holder, course)
    }

    private fun bindData(holder: CourseItemViewHolder, course: Course) = holder.binding.apply {
        tvCourseTitle.text = course.courseTitle
        tvCourseDesc.text = course.courseDesc

        if(course.isComplete) {
            tvCourseComplete.visibility = View.VISIBLE
            pbLevel.visibility = View.GONE

            btnStartCourse.text = "Ôn Tập"
        } else {
            tvCourseComplete.visibility = View.GONE
            pbLevel.visibility = View.VISIBLE

            btnStartCourse.text = "Tiếp Tục"

            pbLevel.max = course.totalLevel
            pbLevel.progress = course.currentLevel
        }

        tvCourseLevel.text = "${course.currentLevel}/${course.totalLevel}"

        btnStartCourse.setOnClickListener{
            itemClickListener.invoke(course)
        }
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseItemViewHolder(var binding: LayoutCourseItemBinding): RecyclerView.ViewHolder(binding.root)
}