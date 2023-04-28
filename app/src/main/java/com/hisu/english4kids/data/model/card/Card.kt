package com.hisu.english4kids.data.model.card

data class Card(
    val id: Int,
    val cardID: Int,
    val imageUrl: String,
    var isVisible: Boolean
)