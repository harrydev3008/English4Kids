package com.hisu.english4kids.data.network

import com.hisu.english4kids.BuildConfig
import com.hisu.english4kids.data.*
import com.hisu.english4kids.data.model.InternetTimeModel
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS).build()

private val retrofitBuilder = Retrofit.Builder()
    .baseUrl(BuildConfig.SERVER_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

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

//  ----------- POST REQUEST -----------
    @POST(PATH_AUTH_LOGIN)
    fun authLogin(@Body body: RequestBody): Call<AuthResponseModel>

    @POST(PATH_AUTH_REGISTER)
    fun authRegister(@Body body: RequestBody): Call<AuthResponseModel>

    @POST(PATH_AUTH_LOGOUT)
    fun authLogout(@Body body: RequestBody): Call<AuthResponseModel>
}