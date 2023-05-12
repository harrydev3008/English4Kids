package com.hisu.english4kids.data.model.card

data class Card(
    val cardId: String = "",
    val pairId: String = "",
    val imageUrl: String = "",
    var isVisible: Boolean = true,
    var isAudio: Boolean = false,
    var word: String = "",
    var answer: String = ""
)