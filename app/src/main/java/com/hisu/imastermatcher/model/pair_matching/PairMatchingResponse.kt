package com.hisu.imastermatcher.model.pair_matching

data class PairMatchingResponse(
    var images: List<PairMatchingModel>,
//    var isAudioQuestion: Boolean = false,
    var question: String,//TODO: audio or word, impl later
    var correctAnswer: String,
    var allowedMoves: Int = 0
//    var audio: String?
)