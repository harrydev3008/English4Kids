package com.hisu.english4kids.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.databinding.LayoutCourseItemBinding
import com.hisu.english4kids.widget.dialog.MessageDialog

class CourseItemViewPagerAdapter(
    var context: Context,
    private val itemClickListener: (course: Course) -> Unit
) : RecyclerView.Adapter<CourseItemViewPagerAdapter.CourseItemViewHolder>() {

    var courses: List<Course> = mutableListOf()
    private val colors = context.resources.getStringArray(R.array.course_color_list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private var imageResources = context.resources.obtainTypedArray(R.array.course_cover_images)

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        val course = courses[position]
        holder.apply {
            binding.imvTempAvatar.setImageResource(imageResources.getResourceId(position, R.drawable.rm_test_4))
            bindData(course, position)
        }
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseItemViewHolder(var binding: LayoutCourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(course: Course, position: Int) = binding.apply {
            tvCourseTitle.text = String.format(context.getString(R.string.course_title_pattern), position + 1, course.title)
            tvCourseDesc.text = course.description

            cardParent.strokeColor = Color.parseColor(colors[position])
            pbCourseProgress.setIndicatorColor(Color.parseColor(colors[position]))
            btnStartCourse.setBackgroundColor(Color.parseColor(colors[position]))

            if (position != 0 && course.currentLevel == 0 && courses[position - 1].currentLevel < courses[position - 1].totalLevel) {
                cardParent.strokeColor = ContextCompat.getColor(context, R.color.light_gray)
                tvProgressNumber.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                tvCourseTitle.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                btnStartCourse.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))

                btnStartCourse.setOnClickListener {
                    MessageDialog(context, context.getString(R.string.course_locked_desc), context.getString(R.string.course_locked), true).showDialog()
                }

            } else {
                btnStartCourse.setOnClickListener {
                    itemClickListener.invoke(course)
                }
            }

            tvProgressNumber.text = String.format(context.getString(R.string.course_progress_pattern), course.currentLevel, course.totalLevel)

            if(course.currentLevel == 0) {
                btnStartCourse.text = context.getString(R.string.start_course)
                pbCourseProgress.max = course.totalLevel
                pbCourseProgress.progress = 0
            } else if(course.currentLevel < course.totalLevel) {
                btnStartCourse.text = context.getString(R.string.next)
                pbCourseProgress.max = course.totalLevel
                pbCourseProgress.progress = course.currentLevel
            } else {
                pbCourseProgress.visibility = View.GONE
                tvCourseComplete.visibility = View.VISIBLE
                btnStartCourse.text = context.getString(R.string.review)
            }
        }
    }
}