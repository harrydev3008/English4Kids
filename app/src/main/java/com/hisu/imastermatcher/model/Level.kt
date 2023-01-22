package com.hisu.imastermatcher.model

data class Level(
    val id: Int,
    val cards: List<Card>,
    /*
    * status:
    *   + -1: locked
    *   +  0: current
    *   +  1: played
    * */
    var status: Int,
    var score: Float
)
