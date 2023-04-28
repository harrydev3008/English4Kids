package com.hisu.english4kids.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.LayoutCourseItemBinding
import com.hisu.english4kids.data.model.course.Course

class CourseItemViewPagerAdapter(
    var context: Context,
    private val itemClickListener: (course: Course) -> Unit
) : RecyclerView.Adapter<CourseItemViewPagerAdapter.CourseItemViewHolder>() {

    var courses: List<Course> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        val course = courses[position]
        holder.apply {
            bindData(course)

            binding.cardParent.setOnClickListener {
                if (!course.isLock)
                    itemClickListener.invoke(course)
            }
        }
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseItemViewHolder(var binding: LayoutCourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(course: Course) = binding.apply {
            tvCourseTitle.text = course.courseTitle
            tvCourseDesc.text = course.courseDesc

            pbCourseProgress.max = course.totalLevel
            pbCourseProgress.progress = course.currentLevel

            if (course.isComplete) {
                setColors(R.color.level_percent_100)
            } else {

                if (course.isLock) {
                    setColors(-1, isLock = true)
                } else {
                    val percentage = (course.currentLevel.toFloat() / course.totalLevel) * 100

                    if (percentage <= 50) {
                        setColors(R.color.level_percent_50)
                    } else if (percentage <= 75) {
                        setColors(R.color.level_percent_75)
                    }
                }
            }

            tvProgressNumber.text = String.format(
                context.getString(R.string.course_progress_pattern),
                course.currentLevel,
                course.totalLevel
            )
        }

        private fun setColors(color: Int, isLock: Boolean = false) = binding.apply {
            if (isLock) {
                cardParent.strokeColor = ContextCompat.getColor(context, R.color.light_gray)
                tvProgressNumber.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
            } else {
                pbCourseProgress.setIndicatorColor(ContextCompat.getColor(context, color))
                cardParent.strokeColor = ContextCompat.getColor(context, color)
                tvProgressNumber.setTextColor(ContextCompat.getColor(context, color))
            }
        }
    }
}