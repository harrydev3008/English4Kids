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

    suspend fun setUserLoinState(loginState: Boolean) {
        mySharedPreferences.putBoolean(MySharedPreferences.USER_LOGIN_STATE, loginState)
    }

    suspend fun getUserLoginState() = mySharedPreferences.getBooleanAsync(MySharedPreferences.USER_LOGIN_STATE)

    suspend fun setUserInfo(userInfo: String) {
        mySharedPreferences.putString(MySharedPreferences.USER_INFO_KEY, userInfo)
    }

    suspend fun getUserInfo() = mySharedPreferences.getString(MySharedPreferences.USER_INFO_KEY)

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
}