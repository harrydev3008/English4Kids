package com.hisu.english4kids.data.network.response_model


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("_id")
    val id: String,
    val phone: String,
    val registerDate: String,
    val username: String,
    var weeklyScore: Int = 0,
    var golds: Int = 0,
    var hearts: Int = 0,
    var claimCount:Int = 0,
    var lastClaimdDate: String = "",
    var level: String = "NEWBIE"
)