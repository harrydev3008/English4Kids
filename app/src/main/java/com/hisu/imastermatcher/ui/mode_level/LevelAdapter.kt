package com.hisu.imastermatcher.ui.mode_level

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.databinding.LayoutLevelCompletedBinding
import com.hisu.imastermatcher.databinding.LayoutLevelCurrentBinding
import com.hisu.imastermatcher.databinding.LayoutLevelLockBinding
import com.hisu.imastermatcher.model.course.CourseLevel

class LevelAdapter(
    val levelClickListener: (level: CourseLevel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<CourseLevel>
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

        val curLevel = items[position]

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

    override fun getItemViewType(position: Int): Int = when (items[position].status) {
        ViewType.LOCKED_TYPE.type -> -1
        ViewType.CURRENT_TYPE.type -> 0
        else -> 1
    }

    override fun getItemCount(): Int = items.size

    inner class CompletedLevelViewHolder(val binding: LayoutLevelCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseLevel) = binding.apply {
            tvLevel.text = "Cửa ${item.id}"
            tvLevelDesc.text = "${item.description}"
            rbLevelRate.rating = item.score
        }
    }

    inner class CurrentLevelViewHolder(val binding: LayoutLevelCurrentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseLevel) = binding.apply {
            tvCurrentLevel.text = "Cửa ${item.id}"
            tvCurrentLevelDesc.text = "${item.description}"
        }
    }

    inner class LockLevelViewHolder(val binding: LayoutLevelLockBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: CourseLevel) = binding.apply {
                tvNextLevelDesc.text = "${item.description}"
            }
        }

    enum class ViewType(val type: Int) {
        LOCKED_TYPE(-1),
        CURRENT_TYPE(0),
        PLAYED_TYPE(1)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CourseLevel>() {
        override fun areItemsTheSame(oldItem: CourseLevel, newItem: CourseLevel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CourseLevel, newItem: CourseLevel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer<CourseLevel>(this, diffCallback)
}