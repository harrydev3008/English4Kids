package com.hisu.english4kids.ui.home

import android.content.Context
import android.graphics.Color
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
    private val colors = context.resources.getStringArray(R.array.course_color_list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private var imageResources = context.resources.getIntArray(R.array.course_cover_images)

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        val course = courses[position]
        holder.apply {
            bindData(course, position)

            holder.binding.imvTempAvatar.setImageResource(imageResources[position])

            binding.btnStartCourse.setOnClickListener {
                if (!course.isLock)//todo: have to check this logic
                    itemClickListener.invoke(course)
            }
        }
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseItemViewHolder(var binding: LayoutCourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(course: Course, position: Int) = binding.apply {
            tvCourseTitle.text = course.title
            tvCourseDesc.text = course.description

            pbCourseProgress.max = course.totalLevel
            pbCourseProgress.progress = course.currentLevel


            cardParent.strokeColor = Color.parseColor(colors[position])
            pbCourseProgress.setIndicatorColor(Color.parseColor(colors[position]))
            btnStartCourse.setBackgroundColor(Color.parseColor(colors[position]))

            if (course.isLock) {
                cardParent.strokeColor = ContextCompat.getColor(context, R.color.light_gray)
                tvProgressNumber.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                tvCourseTitle.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                btnStartCourse.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            }

            tvProgressNumber.text = String.format(
                context.getString(R.string.course_progress_pattern),
                course.currentLevel,
                course.totalLevel
            )

            if(course.currentLevel == 0) {
                btnStartCourse.text = context.getString(R.string.start_course)
            } else if(course.currentLevel < course.totalLevel) {
                btnStartCourse.text = context.getString(R.string.next)
            } else {
                btnStartCourse.text = context.getString(R.string.review)
            }
        }
    }
}