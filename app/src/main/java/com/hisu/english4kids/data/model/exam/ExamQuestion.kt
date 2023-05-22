package com.hisu.english4kids.data.model.exam

data class ExamQuestion(
    val _id: String,
    var answers: List<Answer>,
    var attachment: String,
    var attachmentType: String = "NONE",
    var correctAnswer: String,
    var question: String,
    var score: Int = 0,
    var userPickAnswer: String = ""
)