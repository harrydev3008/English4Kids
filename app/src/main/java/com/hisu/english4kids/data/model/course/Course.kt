package com.hisu.english4kids.data.model.course

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course (
    @PrimaryKey(autoGenerate = false) var _id: String,
    val title: String,
    val totalLevel: Int,
    var currentLevel: Int = 0,
    var isComplete: Boolean = false,
    var isLock: Boolean = false,
    var description: String,
)