package com.hisu.english4kids.ui.lessons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.data.PLAY_STATUS_DONE
import com.hisu.english4kids.data.PLAY_STATUS_FAIL
import com.hisu.english4kids.data.PLAY_STATUS_NONE
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.model.game_play.BaseGamePlay
import com.hisu.english4kids.data.model.game_play.Gameplay
import com.hisu.english4kids.databinding.LayoutLessonHeaderBinding
import com.hisu.english4kids.ui.home.GameplayProgressAdapter

class LessonAdapter(
    var context: Context,
    private val lessonClickListener: (level: Lesson, fromRound: Int, status: Int) -> Unit
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
        fun bindData(lesson: Lesson, lessonPosition: Int) = binding.apply {
            val gson = Gson()

            tvHeader.text = lesson.title
            tvHeaderDesc.text = lesson.description

            val gameProgressAdapter = GameplayProgressAdapter(context) { position, status: Int ->
                run {
                    lessonClickListener.invoke(lesson, position, status)
                }
            }

            val mGamePlays = MutableList(lesson.rounds.size) { Gameplay(-1) }
            val roundsJson = gson.toJson(lesson.rounds)
            val itemType = object : TypeToken<List<BaseGamePlay>>() {}.type
            val baseGamePlays = Gson().fromJson<List<BaseGamePlay>>(roundsJson, itemType)

            if (lessonPosition == 0 || (lessons[lessonPosition - 1].playedRounds == lessons[lessonPosition - 1].totalRounds)) {
                for (i in baseGamePlays.indices) {
                    if (baseGamePlays[i].playStatus == null || baseGamePlays[i].playStatus == PLAY_STATUS_NONE) {
                        mGamePlays[i] = Gameplay(0)
                        break
                    } else if (baseGamePlays[i].playStatus == PLAY_STATUS_DONE) {
                        mGamePlays[i] = Gameplay(1)
                    } else if (baseGamePlays[i].playStatus == PLAY_STATUS_FAIL) {
                        mGamePlays[i] = Gameplay(2)
                    }
                }
            }

            gameProgressAdapter.gameplays = mGamePlays

            rvRounds.adapter = gameProgressAdapter
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