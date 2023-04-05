package com.hisu.imastermatcher.model.card

data class CardsResponse (
    var cards: List<Card>,
    var totalPairs: Int,
    var allowedWrongMoveAmount: Int
)