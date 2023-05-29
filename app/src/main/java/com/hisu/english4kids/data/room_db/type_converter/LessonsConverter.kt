package com.hisu.english4kids.data.room_db.type_converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.data.model.course.Lesson

class LessonsConverter {
    @TypeConverter
    fun objectToJson(lessons: List<Lesson>): String = Gson().toJson(lessons)

    @TypeConverter
    fun jsonToObject(json: String): List<Lesson> {
        val itemType = object : TypeToken<List<Lesson>>() {}.type
        return Gson().fromJson(json, itemType)
    }
}