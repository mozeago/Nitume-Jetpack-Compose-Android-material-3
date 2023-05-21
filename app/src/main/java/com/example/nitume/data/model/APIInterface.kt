package com.example.nitume.data.model

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {
    @GET("posts")
    fun getNitumeTasks(): Call<List<NitumeModel>>
}