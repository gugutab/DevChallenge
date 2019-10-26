package com.tabdeveloper.devchallenge.data.services

import com.tabdeveloper.devchallenge.data.model.VideoModel
import io.reactivex.Single
import retrofit2.http.GET

interface VideoService {
    @GET("url")
    fun getVideoModelList(): Single<List<VideoModel>>
}