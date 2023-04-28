package com.hisu.english4kids.data.model.translate_question

data class TranslateQuestionModel(
    val id: Int,
    var title: String,
    var question: String,
    var answer: String,
    var randomWords: List<String>
)