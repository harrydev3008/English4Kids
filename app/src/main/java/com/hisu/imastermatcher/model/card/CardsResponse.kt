package com.hisu.imastermatcher.model.card

data class CardsResponse (
    var data: List<Card>,
    var totalPairs: Int,
    var allowedMoves: Int
)