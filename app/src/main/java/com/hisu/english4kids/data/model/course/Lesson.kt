package com.hisu.english4kids.data.model.course

data class Lesson(
    var _id: String,
    var title: String,
    /*
    * status:
    *   + -1: locked
    *   +  0: current
    *   +  1: played
    * */
    var status: Int = 0,
    var description: String,
    var totalRounds: Int = 0,
    var playedRounds: Int = 0,
    var rounds: List<Object>
)
