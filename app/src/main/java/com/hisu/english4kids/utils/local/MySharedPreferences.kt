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
    }

    private fun  getSharedPreferences() : SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCE, Context.MODE_PRIVATE
    )

    suspend fun putBoolean(key: String, value: Boolean) {
        val editor = getSharedPreferences().edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    suspend fun putString(key: String, value: String) {
        val editor = getSharedPreferences().edit()
        editor.putString(key, value)
        editor.apply()
    }

    suspend fun getBooleanAsync(key: String) = getSharedPreferences().getBoolean(key, false)

    fun getBoolean(key: String) = getSharedPreferences().getBoolean(key, false)

    suspend fun getString(key: String) = getSharedPreferences().getString(key, "")
}