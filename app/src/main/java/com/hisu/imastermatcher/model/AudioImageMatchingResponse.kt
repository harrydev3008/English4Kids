package com.hisu.imastermatcher.model

data class AudioImageMatchingResponse(
    var images: List<AudioImageMatchingModel>,
    var question: String,//TODO: audio or word, impl later
    var correctAnswer: String
)