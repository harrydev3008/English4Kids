package com.hisu.english4kids.ui.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.LayoutLevelCompletedBinding
import com.hisu.english4kids.databinding.LayoutLevelCurrentBinding
import com.hisu.english4kids.databinding.LayoutLevelLockBinding
import com.hisu.english4kids.model.course.Lesson

class LessonAdapter(
    var context: Context,
    private val levelClickListener: (level: Lesson) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lessons: List<Lesson>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (p) {
            ViewType.LOCKED_TYPE.type -> {
                val view = LayoutLevelLockBinding.inflate(inflater, parent, false)
                LockLevelViewHolder(view)
            }

            ViewType.CURRENT_TYPE.type -> {
                val view = LayoutLevelCurrentBinding.inflate(inflater, parent, false)
                CurrentLevelViewHolder(view)
            }

            else -> {
                val view = LayoutLevelCompletedBinding.inflate(inflater, parent, false)
                CompletedLevelViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val curLevel = lessons[position]

        when (holder) {
            is CompletedLevelViewHolder -> {
                holder.bind(curLevel)
                holder.binding.levelParent.setOnClickListener {
                    levelClickListener.invoke(curLevel)
                }
            }

            is CurrentLevelViewHolder -> {
                holder.bind(curLevel)
                holder.binding.levelParent.setOnClickListener {
                    levelClickListener.invoke(curLevel)
                }
            }

            else -> {
                //todo: current lock level
                (holder as LockLevelViewHolder).bind(curLevel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (lessons[position].status) {
        ViewType.LOCKED_TYPE.type -> -1
        ViewType.CURRENT_TYPE.type -> 0
        else -> 1
    }

    override fun getItemCount(): Int = lessons.size

    inner class CompletedLevelViewHolder(val binding: LayoutLevelCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lesson) = binding.apply {
            tvLevel.text = String.format(context.getString(R.string.lesson_pattern), item.id)
            tvLevelDesc.text = item.description
            rbLevelRate.rating = item.score
        }
    }

    inner class CurrentLevelViewHolder(val binding: LayoutLevelCurrentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lesson) = binding.apply {
            tvCurrentLevel.text = String.format(context.getString(R.string.lesson_pattern), item.id)
            tvCurrentLevelDesc.text = item.description
        }
    }

    inner class LockLevelViewHolder(val binding: LayoutLevelLockBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Lesson) = binding.apply {
                tvNextLevelDesc.text = item.description
            }
        }

    enum class ViewType(val type: Int) {
        LOCKED_TYPE(-1),
        CURRENT_TYPE(0),
        PLAYED_TYPE(1)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer<Lesson>(this, diffCallback)
}