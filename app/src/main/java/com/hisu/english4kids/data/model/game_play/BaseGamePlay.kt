package com.hisu.english4kids.data.model.game_play

open class BaseGamePlay {
    var roundId: String = ""
    var playType: Int = 1
    var score: Int = 0
    var playStatus: String? = "NONE"
        get() = field ?: "NONE"
        set(value) {
            if (value != null) {
                field = value
            } else {
                field = "NONE"
            }
        }

    constructor(roundId: String, playType: Int, score: Int)
}