package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.course.Round

class GameStyleThree (
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String,
    var isAudio: Boolean = false,
    var randomWords: List<String>
) : Round(playType, score)