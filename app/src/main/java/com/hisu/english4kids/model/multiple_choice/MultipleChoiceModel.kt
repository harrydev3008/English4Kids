package com.hisu.english4kids.model.multiple_choice

data class MultipleChoiceModel(
    var id: Int,
    var question: String,
    var choices: List<String>,
    var correctAnswer: String,
)