package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleTwo(
    roundId: String,
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String,
    var isPlayed: Boolean = false,
    var cards: List<Card>
): BaseGamePlay(roundId, playType, score)