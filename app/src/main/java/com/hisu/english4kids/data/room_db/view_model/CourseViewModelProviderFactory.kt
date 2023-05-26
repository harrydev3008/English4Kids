package com.hisu.english4kids.data.room_db.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hisu.english4kids.data.room_db.repository.CourseRepository
import com.hisu.english4kids.data.room_db.repository.PlayerRepository

class CourseViewModelProviderFactory(private val courseRepository: CourseRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CourseViewModel::class.java))
            return CourseViewModel(courseRepository) as T
        throw IllegalArgumentException("Unknown Class Name Exception")
    }
}