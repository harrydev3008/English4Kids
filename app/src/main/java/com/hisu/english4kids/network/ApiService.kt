package com.hisu.english4kids.network

import com.hisu.english4kids.BuildConfig
import com.hisu.english4kids.data.network.APIService
import com.hisu.english4kids.model.InternetTimeModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

//todo: impl later
//private val requestInterceptor = Interceptor { chain ->
//
//    val url = chain.request().url().newBuilder()
//        .addQueryParameter("api_key", BuildConfig.API_KEY)
//        .build()
//
//    val request = chain.request().newBuilder().url(url).build()
//
//    return@Interceptor chain.proceed(request)
//}

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS).build()

private val retrofitBuilder = Retrofit.Builder()
    .baseUrl(BuildConfig.INTERNET_TIME_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create()).build()

object API {
    val apiService: ApiService by lazy {
        retrofitBuilder.create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("api/timezone/Asia/Ho_Chi_Minh/")
    suspend fun getInternetTime(): Call<InternetTimeModel>
}