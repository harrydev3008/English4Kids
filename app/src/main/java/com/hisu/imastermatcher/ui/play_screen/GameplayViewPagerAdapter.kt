package com.hisu.imastermatcher.ui.play_screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hisu.imastermatcher.ui.play_style.MatchingAudioImagePairsFragment
import com.hisu.imastermatcher.ui.play_style.MatchingWordPairsFragment
import com.hisu.imastermatcher.ui.play_style.SentenceStyleFragment
import com.hisu.imastermatcher.ui.play_style.TypeAnswerFragment

class GameplayViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val itemTapListener: () -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    var gameplays = listOf<String>()

    override fun getItemCount(): Int = gameplays.size

    override fun createFragment(position: Int): Fragment {
        //TODO: return gameplay for each gameplay style
        val curGameplay = gameplays[position]

        if (curGameplay == "sentence") {
            return SentenceStyleFragment(itemTapListener)
        }

        if (curGameplay == "word_pair") {
//            return SentenceStyleFragment(itemTapListener)
            return MatchingWordPairsFragment(itemTapListener)
        }

        if (curGameplay == "audio_image_pair") {
//            return SentenceStyleFragment(itemTapListener)
            return MatchingAudioImagePairsFragment(itemTapListener)
        }

        return TypeAnswerFragment(itemTapListener)
    }
}