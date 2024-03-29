package com.hisu.english4kids.utils.local

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(var context: Context) {

    companion object {
        val SHARED_PREFERENCE = "E4K_SHARED_PREFERENCE"
        val USER_INFO_KEY = "USER_INFO"
        val USER_LOGIN_STATE = "USER_LOGIN_STATE"
        val USER_REMIND_LEARNING_STATE = "USER_REMIND_LEARNING_STATE"
        val USER_REMIND_DAILY_REWARD_STATE = "USER_REMIND_DAILY_REWARD_STATE"
        val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"
        val USER_REFRESH_TOKEN = "USER_REFRESH_TOKEN"
        val COURSE_INFO = "COURSE_INFO"
        val LESSON_INFO = "LESSON_INFO"
    }

    private fun  getSharedPreferences() : SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCE, Context.MODE_PRIVATE
    )

    fun putBoolean(key: String, value: Boolean) {
        val editor = getSharedPreferences().edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putString(key: String, value: String) {
        val editor = getSharedPreferences().edit()
        editor.putString(key, value)
        editor.apply()
    }

    suspend fun getBooleanAsync(key: String) = getSharedPreferences().getBoolean(key, false)

    fun getBoolean(key: String) = getSharedPreferences().getBoolean(key, false)

    fun getString(key: String) = getSharedPreferences().getString(key, "")
}