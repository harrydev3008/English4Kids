package com.hisu.english4kids.ui.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.model.game_play.Gameplay
import com.hisu.english4kids.databinding.LayoutLessonHeaderBinding
import com.hisu.english4kids.ui.home.GameplayProgressAdapter

class LessonAdapter(
    var context: Context,
    private val lessonClickListener: (level: Lesson, fromRound: Int) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonsViewHolder>() {

    var lessons: List<Lesson>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): LessonAdapter.LessonsViewHolder {
        return LessonsViewHolder(
            LayoutLessonHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LessonAdapter.LessonsViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.bindData(lesson, position)

    }

    override fun getItemCount(): Int = lessons.size

    inner class LessonsViewHolder(var binding: LayoutLessonHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(lesson: Lesson, index: Int) = binding.apply {
            tvHeader.text = String.format(
                context.getString(R.string.lesson_pattern),
                index + 1
            )

            tvHeaderDesc.text = lesson.description

            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.spanSizeLookup = spanSizeLookUp

            val gameProgressAdapter = GameplayProgressAdapter(context) { position ->
                lessonClickListener.invoke(lesson, position)
            }

            val mGamePlays = mutableListOf<Gameplay>()

            for(i in 0 until lesson.rounds.size) {
                if(i % 2 == 0)
                    mGamePlays.add( Gameplay(1))
                else
                    mGamePlays.add( Gameplay(0))
            }

            gameProgressAdapter.gameplays = mGamePlays

            rvRounds.layoutManager = gridLayoutManager
            rvRounds.adapter = gameProgressAdapter
        }
    }

    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (position == 0 || position % 5 == 0) return 2
            return 1
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem._id == newItem._id
        }
    }

    private val differ = AsyncListDiffer<Lesson>(this, diffCallback)
}