package com.hisu.english4kids.data.model.game_play

class GameStyleFour (
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String
) : BaseGamePlay(playType, score)