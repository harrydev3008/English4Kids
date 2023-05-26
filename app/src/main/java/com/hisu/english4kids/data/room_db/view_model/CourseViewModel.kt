package com.hisu.english4kids.data.room_db.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.room_db.repository.CourseRepository
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseViewModel(val courseRepository: CourseRepository) : ViewModel()  {

    fun getCourse() = courseRepository.getCourse()

    fun insertCourse(course: Course) = viewModelScope.launch(Dispatchers.IO) {
        courseRepository.insertCourse(course)
    }

    fun insertCourses(courses: List<Course>) = viewModelScope.launch(Dispatchers.IO) {
        courses.forEach {
            courseRepository.insertCourse(it)
        }
    }

    fun updateCourse(course: Course) = viewModelScope.launch(Dispatchers.IO) {
        courseRepository.updateCourse(course)
    }
}