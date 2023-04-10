package com.hisu.imastermatcher.model.leader_board

data class LeaderBoardUser(
    val rank: Int,
    val totalScore: Int,
    val userAvatar: String,
    val username: String
)