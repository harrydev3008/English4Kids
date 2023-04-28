package com.hisu.english4kids.data.model.pair_matching

data class PairMatchingResponse(
    var data: List<PairMatchingModel>,
    var question: String,
    var correctAnswer: String,
    var allowedMoves: Int = 0
)