package com.hisu.imastermatcher.model

data class CardsResponse (
    var cards: List<Card>,
    var totalPairs: Int,
    var allowedWrongMoveAmount: Int
)