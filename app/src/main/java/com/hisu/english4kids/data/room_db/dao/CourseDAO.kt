package com.hisu.english4kids.data.room_db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisu.english4kids.data.model.course.Course

@Dao
interface CourseDAO {
    @Query("SELECT * FROM courses")
    fun getCourses(): LiveData<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course)

    @Update
    suspend fun update(course: Course)

    @Query("DELETE FROM courses")
    suspend fun dropCourseTable()

    @Delete
    suspend fun delete(course: Course)
}