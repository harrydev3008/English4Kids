package com.hisu.english4kids.data.room_db.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.room_db.dao.CourseDAO
import com.hisu.english4kids.data.room_db.dao.PlayerDAO

class CourseRepository(context: Context, private val courseDAO: CourseDAO) {
    fun getCourse(): LiveData<List<Course>> = courseDAO.getCourses()

    suspend fun insertCourse(course: Course) = courseDAO.insert(course)

    suspend fun updateCourse(course: Course) = courseDAO.update(course)

    suspend fun deleteCourse(course: Course) = courseDAO.delete(course)
}