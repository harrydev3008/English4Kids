package com.hisu.english4kids.data.model.game_play

class GameStyleThree (
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String,
    var isAudio: Boolean = false,
    var randomWords: List<String>
) : BaseGamePlay(playType, score)