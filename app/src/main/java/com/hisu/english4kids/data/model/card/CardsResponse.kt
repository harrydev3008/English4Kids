package com.hisu.english4kids.data.model.card

data class CardsResponse (
    var data: List<Card>,
    var totalPairs: Int,
    var allowedMoves: Int
)