package com.hisu.english4kids.data.room_db.type_converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.model.course.Lesson

class RoundsConverter {
    @TypeConverter
    fun objectToJson(lessons: List<Any>): String = Gson().toJson(lessons)

    @TypeConverter
    fun jsonToObject(json: String): List<Any> {
        val itemType = object : TypeToken<List<Any>>() {}.type
        return Gson().fromJson(json, itemType)
    }
}