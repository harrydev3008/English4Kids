package com.hisu.english4kids.ui.play_screen

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.data.model.game_play.GameStyleOne
import com.hisu.english4kids.ui.play_screen.gameplay.audio_image.MatchingAudioImagePairsFragment
import com.hisu.english4kids.ui.play_screen.gameplay.audio_word.MatchingAudioWordFragment
import com.hisu.english4kids.ui.play_screen.gameplay.class_pairs_matching.ClassicPairsMatchingFragment
import com.hisu.english4kids.ui.play_screen.gameplay.complete_sentence.SentenceStyleFragment
import com.hisu.english4kids.ui.play_screen.gameplay.match_pairs.MatchingWordPairsFragment
import com.hisu.english4kids.ui.play_screen.gameplay.type_answer.TypeAnswerFragment

class GameplayViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    var gameplays = listOf<Object>()

    fun setGamePlays(gamePlays: List<Object>) {
        this.gameplays = gamePlays
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = gameplays.size

    override fun createFragment(position: Int): Fragment {
        val curGameplay = gameplays[position]

        val gson = Gson()
        val obj = gson.fromJson(gson.toJsonTree(curGameplay), JsonObject::class.java)
        val gameType = "${obj.get("playType").toString()[0]}".toInt()

        if (gameType == 1) {
            val gameTypeOne = gson.fromJson(obj, GameStyleOne::class.java)
            return ClassicPairsMatchingFragment(itemTapListener, wrongAnswerListener, gameTypeOne)
        }

        if (gameType == 2) {
            return MatchingAudioImagePairsFragment(itemTapListener, wrongAnswerListener)
        }

        if (gameType == 3) {
            return SentenceStyleFragment(itemTapListener, wrongAnswerListener)
        }

        if (gameType == 4) {
            return TypeAnswerFragment(itemTapListener, wrongAnswerListener)
        }

        if (gameType == 5) {
            return MatchingAudioWordFragment(itemTapListener, wrongAnswerListener)
        }

        return MatchingWordPairsFragment(itemTapListener, wrongAnswerListener)
    }
}