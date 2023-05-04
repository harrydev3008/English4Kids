package com.hisu.english4kids.data.network.response_model


data class Data(
    val accessToken: String,
    val player: Player?,
    val refreshToken: String
)