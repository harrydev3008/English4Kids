package com.hisu.imastermatcher.model

data class SentenceQuestion(
    val id: Int,
    var title: String,
    var question: String,
    var answer: String,
    var randomWords: List<String>
)