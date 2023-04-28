package com.hisu.english4kids.data.model.multiple_choice

data class MultipleChoiceModel(
    var id: Int,
    var question: String,
    var choices: List<String>,
    var correctAnswer: String,
)