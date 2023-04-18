package com.hisu.english4kids.ui.gameplay.multiple_choice

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hisu.english4kids.model.multiple_choice.MultipleChoicesResponse

class MultipleChoiceViewPagerAdapter (
    fragmentActivity: FragmentActivity,
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    var questions = MultipleChoicesResponse()

    override fun getItemCount(): Int = questions.size

    override fun createFragment(position: Int): Fragment {
        return MultipleChoiceFragment(itemTapListener, wrongAnswerListener, questions[position])
    }

}