package com.hisu.imastermatcher.model

data class Course (
    val courseTitle: String,
    val totalLevel: Int,
    var currentLevel: Int = 0,
    var isComplete: Boolean = false,
    var courseDesc: String
)