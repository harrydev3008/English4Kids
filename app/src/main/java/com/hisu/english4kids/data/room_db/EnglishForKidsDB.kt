package com.hisu.english4kids.data.room_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.room_db.dao.CourseDAO
import com.hisu.english4kids.data.room_db.dao.PlayerDAO

@Database(
    entities = [Player::class, Course::class],
    version = 1,
    exportSchema = false
)
abstract class EnglishForKidsDB: RoomDatabase() {
    companion object {

        @Volatile
        private var instance: EnglishForKidsDB? = null

        fun getDatabase(context: Context): EnglishForKidsDB = synchronized(this) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnglishForKidsDB::class.java, "EnglishForKidsDB"
                ).build()
            }
            return instance!!
        }
    }

    abstract fun playerDAO(): PlayerDAO
    abstract fun courseDAO(): CourseDAO
}