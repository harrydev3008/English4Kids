package com.hisu.english4kids.data.network.response_model


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("_id")
    val id: String,
    val level: Int,
    val phone: String,
    val registerDate: String,
    val role: String,
    val username: String,
    val weeklyScore: Int
)