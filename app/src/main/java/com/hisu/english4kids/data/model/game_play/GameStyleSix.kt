package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleSix  (
    roundId: String,
    playType: Int,
    score: Int,
    var question: String,
    var cards: List<Card>,
    var allowedMoves: Int = 0,
    var isPlayed: Boolean = false,
    var totalPairs: Int = 0,
) : BaseGamePlay(roundId, playType, score)