package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleOne(
    roundId: String,
    playType: Int,
    score: Int,
    var allowedMoves: Int,
    var totalPairs: Int,
    var isPlayed: Boolean = false,
    var cards: List<Card>
) : BaseGamePlay(roundId, playType, score)