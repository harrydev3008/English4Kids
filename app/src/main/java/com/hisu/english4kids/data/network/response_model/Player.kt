package com.hisu.english4kids.data.network.response_model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "player")
data class Player(
    @SerializedName("_id")
    @PrimaryKey(autoGenerate = false) val id: String,
    val phone: String,
    val registerDate: String,
    val username: String,
    var weeklyScore: Int = 0,
    var golds: Int = 0,
    var hearts: Int = 0,
    var claimCount:Int = 0,

    @ColumnInfo(name = "lastClaimdDate", defaultValue = "")
    var lastClaimdDate: String = ""
)