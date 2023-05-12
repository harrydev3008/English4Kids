package com.hisu.english4kids.ui.play_screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.data.model.game_play.*
import com.hisu.english4kids.ui.play_screen.gameplay.audio_image.MatchingAudioImagePairsFragment
import com.hisu.english4kids.ui.play_screen.gameplay.audio_word.MatchingAudioWordFragment
import com.hisu.english4kids.ui.play_screen.gameplay.class_pairs_matching.ClassicPairsMatchingFragment
import com.hisu.english4kids.ui.play_screen.gameplay.complete_sentence.SentenceStyleFragment
import com.hisu.english4kids.ui.play_screen.gameplay.match_pairs.MatchingWordPairsFragment
import com.hisu.english4kids.ui.play_screen.gameplay.type_answer.TypeAnswerFragment

class GameplayViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val nextRoundListener: () -> Unit,
    private val wrongAnswerListener: (position: Int) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String) -> Unit
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
            val gameStyleOne = gson.fromJson(obj, GameStyleOne::class.java)
            return ClassicPairsMatchingFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleOne, position)
        }

        if (gameType == 2) {
            val gameStyleTwo = gson.fromJson(obj, GameStyleTwo::class.java)
            return MatchingAudioImagePairsFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleTwo, position)
        }

        if (gameType == 3) {
            val gameStyleThree = gson.fromJson(obj, GameStyleThree::class.java)
            return SentenceStyleFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleThree, position)
        }

        if (gameType == 4) {
            val gameStyleFour = gson.fromJson(obj, GameStyleFour::class.java)
            return TypeAnswerFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleFour, position)
        }

        if (gameType == 5) {
            val gameStyleFive = gson.fromJson(obj, GameStyleFive::class.java)
            return MatchingAudioWordFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleFive, position)
        }

        val gameStyleSix = gson.fromJson(obj, GameStyleSix::class.java)
        return MatchingWordPairsFragment(nextRoundListener, wrongAnswerListener, correctAnswerListener, gameStyleSix, position)
    }
}