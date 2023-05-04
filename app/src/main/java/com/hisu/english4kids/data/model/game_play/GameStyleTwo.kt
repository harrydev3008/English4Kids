package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleTwo(
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String,
    var cards: List<Card>
): BaseGamePlay(playType, score)