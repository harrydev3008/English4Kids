package com.hisu.english4kids.utils.local

import android.content.Context

class LocalDataManager {

    private lateinit var context: Context

    fun init(context: Context) {
        LocalDataManager@this.context = context
    }

    private val mySharedPreferences: MySharedPreferences by lazy {
        MySharedPreferences(context)
    }

    fun setUserLoinState(loginState: Boolean) {
        mySharedPreferences.putBoolean(MySharedPreferences.USER_LOGIN_STATE, loginState)
    }

    fun getUserLoginState() = mySharedPreferences.getBoolean(MySharedPreferences.USER_LOGIN_STATE)

    fun setUserInfo(userInfo: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_INFO_KEY, userInfo)
    }

    fun getUserInfo() = mySharedPreferences.getString(MySharedPreferences.USER_INFO_KEY)

    fun setUserAccessToken(token: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_ACCESS_TOKEN, token)
    }

    fun getUserAccessToken() =
        mySharedPreferences.getString(MySharedPreferences.USER_ACCESS_TOKEN)

    fun setUserRefreshToken(token: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_REFRESH_TOKEN, token)
    }

    fun getUserRefreshToken() = mySharedPreferences.getString(MySharedPreferences.USER_REFRESH_TOKEN)

    fun setLessonsInfo(lesson: String) {
        mySharedPreferences.putString(MySharedPreferences.LESSON_INFO, lesson)
    }
}