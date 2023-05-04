package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleSix  (
    playType: Int,
    score: Int,
    var question: String,
    var cards: List<Card>,
    var allowedMoves: Int = 0,
    var totalPairs: Int = 0,
) : BaseGamePlay(playType, score)