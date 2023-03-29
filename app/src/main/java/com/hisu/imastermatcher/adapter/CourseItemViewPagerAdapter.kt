package com.hisu.imastermatcher.adapter

import android.graphics.Color
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

    private val pageColors = listOf<String>(
        "#02AF84", "#FE63BD", "#20B0F5"
    )

    private var holders = mutableListOf<CourseItemViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        holders.add(holder)
        val course = courses[position]
        bindData(holder, course)
    }

    fun changePage(idx: Int, color: String) {
        holders.forEach {
            it.binding.courseCardContainer.setCardBackgroundColor(Color.parseColor(color))
            it.binding.btnStartCourse.setBackgroundColor(Color.parseColor(color))
        }
        holders[idx].binding.pbLevel.setIndicatorColor(Color.parseColor(color))
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