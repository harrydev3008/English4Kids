package com.hisu.english4kids.data.model.game_play

class GameStyleFour (
    roundId: String,
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String
) : BaseGamePlay(roundId, playType, score)