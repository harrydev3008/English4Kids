package com.hisu.english4kids

import android.app.Application
import com.hisu.english4kids.data.room_db.EnglishForKidsDB

class MyApplication : Application() {
    val database: EnglishForKidsDB by lazy { EnglishForKidsDB.getDatabase(this) }
}