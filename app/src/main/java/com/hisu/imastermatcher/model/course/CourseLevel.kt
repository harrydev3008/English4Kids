package com.hisu.imastermatcher.model.course

data class CourseLevel(
    val id: Int,
    /*
    * status:
    *   + -1: locked
    *   +  0: current
    *   +  1: played
    * */
    var status: Int,
    var description: String,
    var score: Float
)
