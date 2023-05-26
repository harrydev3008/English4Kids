package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hisu.english4kids.data.model.exam.Answer
import com.hisu.english4kids.data.model.exam.ExamQuestion

class MultipleChoiceViewPagerAdapter (
    fragmentActivity: FragmentActivity,
    private val nextQuestionListener: (answer: Answer, position: Int, isCorrect: Boolean) -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    var questions = listOf<ExamQuestion>()

    override fun getItemCount(): Int = questions.size

    override fun createFragment(position: Int): Fragment {
        return MultipleChoiceFragment(nextQuestionListener, questions[position], position)
    }
}