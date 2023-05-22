package com.hisu.english4kids.data.network

import com.google.gson.GsonBuilder
import com.hisu.english4kids.BuildConfig
import com.hisu.english4kids.data.*
import com.hisu.english4kids.data.model.InternetTimeModel
import com.hisu.english4kids.data.network.response_model.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .callTimeout(2, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS).build()

private val retrofitBuilder = Retrofit.Builder()
    .baseUrl(BuildConfig.SERVER_URL)
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(
        GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()
        )
    ).build()

object API {
    val apiService: ApiService by lazy {
        retrofitBuilder.create(ApiService::class.java)
    }
}

interface ApiService {
    //  ----------- GET REQUEST -----------
    @GET(PATH_INTERNET_TIME)
    fun getInternetTime(): Call<InternetTimeModel>

    @GET(PATH_SEARCH_USER_BY_PHONE)
    fun searchUserByPhone(@Query("phone") phone: String): Call<SearchUserResponseModel>

    @GET(PATH_GET_COURSE)
    fun getCourses(@Header("Authorization") token: String): Call<CourseResponseModel>

    @GET(PATH_GET_LESSON_BY_COURSE_ID)
    fun getLessonByCourseId(@Header("Authorization") token: String, @Path("courseId") courseId: String): Call<LessonResponseModel>

    @GET(PATH_GET_WEEKLY_RANK)
    fun getLeaderBoard(@Header("Authorization") token: String): Call<LeaderBoardResponseModel>

    @GET(PATH_GET_USER_INFO)
    fun getUserInfo(@Header("Authorization") token: String): Call<SearchUserResponseModel>

    @GET(PATH_GET_EXAM)
    fun getExam(@Header("Authorization") token: String, @Query("userLevel") userLevel: String): Call<ExamResponseModel>

    //  ----------- POST REQUEST -----------
    @POST(PATH_AUTH_LOGIN)
    fun authLogin(@Body body: RequestBody): Call<AuthResponseModel>

    @POST(PATH_AUTH_REGISTER)
    fun authRegister(@Body body: RequestBody): Call<AuthResponseModel>

    @POST(CHECK_SSO)
    fun checkSSO(@Body body: RequestBody): Call<AuthResponseModel>

    @POST(PATH_AUTH_LOGOUT)
    fun authLogout(@Body body: RequestBody): Call<AuthResponseModel>

    @Headers("Content-Type: application/json")
    @POST(PATH_UPDATE_USER_DIARY)
    fun updateDiary(@Header("Authorization") token: String, @Body body: HashMap<String, Any>): Call<ResponseBody>

    @POST(PATH_BUY_HEART)
    fun buyHeart(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>

    @POST(PATH_UPDATE_GOLDS)
    fun updateGolds(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>

    //  ----------- PUT REQUEST -----------
    @PUT(PATH_UPDATE_USER_INFO)
    fun updateUserInfo(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>

    @PUT(PATH_AUTH_CHANGE_PASSWORD)
    fun changePassword(@Header("Authorization") token: String, @Body body: RequestBody): Call<AuthResponseModel>

    @PUT(PATH_AUTH_FORGOT_PASSWORD)
    fun recoverPassword(@Body body: RequestBody): Call<AuthResponseModel>

    @PUT(PATH_DAILY)
    fun claimDailyReward(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>

    @PUT(PATH_UPDATE_HEART)
    fun updateHeart(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>

    @PUT(PATH_UPDATE_SCORE_AND_GOLDS)
    fun updateScoreAndGold(@Header("Authorization") token: String, @Body body: RequestBody): Call<UpdateUserResponseModel>
}