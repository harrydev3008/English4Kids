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

    suspend fun getUserLoginStateAsync() = mySharedPreferences.getBooleanAsync(MySharedPreferences.USER_LOGIN_STATE)

    fun getUserLoginState() = mySharedPreferences.getBoolean(MySharedPreferences.USER_LOGIN_STATE)

    fun setUserInfo(userInfo: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_INFO_KEY, userInfo)
    }

    fun getUserInfo() = mySharedPreferences.getString(MySharedPreferences.USER_INFO_KEY)

    suspend fun setUserRemindLearningState(learningRemindState: Boolean) {
        mySharedPreferences.putBoolean(MySharedPreferences.USER_REMIND_LEARNING_STATE, learningRemindState)
    }

    fun getUserRemindLearningState() =
         mySharedPreferences.getBoolean(MySharedPreferences.USER_REMIND_LEARNING_STATE)

    suspend fun setUserRemindDailyRewardState(dailyRemindState: Boolean) {
        mySharedPreferences.putBoolean(MySharedPreferences.USER_REMIND_DAILY_REWARD_STATE, dailyRemindState)
    }

    fun getUserRemindDailyRewardState() =
        mySharedPreferences.getBoolean(MySharedPreferences.USER_REMIND_DAILY_REWARD_STATE)

    fun setUserAccessToken(token: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_ACCESS_TOKEN, token)
    }

    fun getUserAccessToken() =
        mySharedPreferences.getString(MySharedPreferences.USER_ACCESS_TOKEN)

    fun setUserRefreshToken(token: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_REFRESH_TOKEN, token)
    }

    fun getUserRefreshToken() =
        mySharedPreferences.getString(MySharedPreferences.USER_REFRESH_TOKEN)

    fun setCourseInfo(course: String) {
        mySharedPreferences.putString(MySharedPreferences.COURSE_INFO, course)
    }

    fun getCourseInfo() = mySharedPreferences.getString(MySharedPreferences.COURSE_INFO)

    fun setLessonsInfo(lesson: String) {
        mySharedPreferences.putString(MySharedPreferences.LESSON_INFO, lesson)
    }

    fun getLessonsInfo() = mySharedPreferences.getString(MySharedPreferences.LESSON_INFO)
}