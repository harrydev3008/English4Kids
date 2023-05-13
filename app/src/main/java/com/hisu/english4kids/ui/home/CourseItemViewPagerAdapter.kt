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

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        val course = courses[position]
        holder.apply {
            bindData(course, position)

            if(position == 0) {
                holder.binding.imvTempAvatar.setImageResource(R.drawable.test_rm)
            } else if(position == 1) {
                holder.binding.imvTempAvatar.setImageResource(R.drawable.test_rm_2)
            } else if(position == 2) {
                holder.binding.imvTempAvatar.setImageResource(R.drawable.test_rm_3)
            } else if(position == 3) {
                holder.binding.imvTempAvatar.setImageResource(R.drawable.rm_test_4)
            } else {
                holder.binding.imvTempAvatar.setImageResource(R.drawable.test_rm_3)
            }

            binding.btnStartCourse.setOnClickListener {
                if (!course.isLock)
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

            if (course.isComplete) {
                btnStartCourse.text = context.getString(R.string.review)
            } else {

                cardParent.strokeColor = Color.parseColor(colors[position])
                pbCourseProgress.setIndicatorColor(Color.parseColor(colors[position]))
                btnStartCourse.setBackgroundColor(Color.parseColor(colors[position]))

                if (course.isLock) {
                    cardParent.strokeColor = ContextCompat.getColor(context, R.color.light_gray)
                    tvProgressNumber.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                    tvCourseTitle.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                    btnStartCourse.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
                }
            }

            tvProgressNumber.text = String.format(
                context.getString(R.string.course_progress_pattern),
                course.currentLevel,
                course.totalLevel
            )
        }
    }
}