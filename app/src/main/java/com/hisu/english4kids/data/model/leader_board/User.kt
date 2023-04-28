package com.hisu.english4kids.data.model.leader_board

data class User(
    var id: Int,
    var username: String,
    var phoneNumber: String,
    var weeklyScore: Double = 0.0,
    var weeklyRank: Int = 0
)