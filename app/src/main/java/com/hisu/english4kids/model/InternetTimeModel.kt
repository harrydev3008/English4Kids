package com.hisu.english4kids.model


import com.google.gson.annotations.SerializedName

data class InternetTimeModel(
    val datetime: String,
    @SerializedName("day_of_week")
    val dayOfWeek: Int,
    @SerializedName("day_of_year")
    val dayOfYear: Int,
)