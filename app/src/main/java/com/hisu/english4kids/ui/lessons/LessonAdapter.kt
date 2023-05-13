package com.hisu.english4kids.ui.lessons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.course.Lesson
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

            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.spanSizeLookup = spanSizeLookUp

            val gameProgressAdapter = GameplayProgressAdapter(context) { position, status: Int ->
                run {
                    lessonClickListener.invoke(lesson, position, status)
                }
            }

            val mGamePlays = mutableListOf<Gameplay>()

            if(lessonPosition != 0 && lessons[lessonPosition - 1].playedRounds <  lessons[lessonPosition - 1].totalRounds) {
                for(i in 0 until lesson.rounds.size) {
                    mGamePlays.add(Gameplay(-1))
                }
            } else {
                for(i in 0 until lesson.rounds.size) {
                    val obj = gson.fromJson(gson.toJsonTree(lesson.rounds[i]), JsonObject::class.java)

                    if(obj.get("isPlayed") != null) {
                        if ((obj.get("isPlayed").toString()).toBoolean())
                            mGamePlays.add(Gameplay(1))
                        else {

                            if (i == 0) {
                                mGamePlays.add(Gameplay(0))
                            } else if(i != 0) {
                                val prevObj = gson.fromJson(gson.toJsonTree(lesson.rounds[i - 1]), JsonObject::class.java)
                                if((prevObj.get("isPlayed").toString()).toBoolean()) {
                                    mGamePlays.add(Gameplay(0))
                                } else {
                                    mGamePlays.add(Gameplay(-1))
                                }
                            }else {
                                mGamePlays.add(Gameplay(-1))
                            }
                        }
                    } else {
                        if((lesson.playedRounds == 0 && i == 0) || (lesson.playedRounds < lesson.totalRounds && i == lesson.playedRounds - 1))
                            mGamePlays.add(Gameplay(0))
                        else
                            mGamePlays.add(Gameplay(-1))
                    }
                }
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