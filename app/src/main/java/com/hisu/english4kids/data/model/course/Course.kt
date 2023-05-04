package com.hisu.english4kids.data.model.course

data class Course (
    var _id: String,
    val title: String,
    val totalLevel: Int,
    var currentLevel: Int = 0,
    var isComplete: Boolean = false,
    var isLock: Boolean = false,
    var description: String,
    var lessons: List<Lesson>
)