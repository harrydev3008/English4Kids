package com.hisu.english4kids.data.model.result

data class FinalResult(
    var fastScore: String,
    var perfectScore: Int = 0,
    var totalScore: Int = 0,
    var golds: Int = 0,
    var correctCount: String = ""
)