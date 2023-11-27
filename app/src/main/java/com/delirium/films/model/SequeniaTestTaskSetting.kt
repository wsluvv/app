package com.delirium.films.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object SequeniaTestTaskSetting {
    private const val BASE_URL = "https://s3-eu-west-1.amazonaws.com/sequeniatesttask/"
    val filmsRequest: FilmsRequest
        get() = RetrofitClient.getClient(BASE_URL).create(FilmsRequest::class.java)
}

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}

interface FilmsRequest {
    @GET("films.json")
    fun films(): Call<FilmList>
}