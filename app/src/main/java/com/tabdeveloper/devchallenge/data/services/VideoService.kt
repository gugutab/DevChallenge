package com.tabdeveloper.devchallenge.data.services

import com.tabdeveloper.devchallenge.application.AppConfiguration
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {
    @GET("assets.json")
    fun getVideoModelList(
        @Query("alt") media: String = AppConfiguration.alt,
        @Query("token") token: String = AppConfiguration.token
    ): Single<VideoListModel>

    companion object{
        fun getVideoModelListMock(): Single<VideoListModel>{
            val videoModel = VideoModel("Title", "https://ak3.picdn.net/shutterstock/videos/1011845243/preview/stock-footage--d-render-abstract-seamless-background-looped-animation-fluorescent-ultraviolet-light-glowing.mp4",
                "https://miro.medium.com/max/3000/1*MI686k5sDQrISBM6L8pf5A.jpeg", "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3")
            val list = VideoListModel(listOf(videoModel, videoModel, videoModel, videoModel, videoModel))
            return Single.just(list)
        }
    }
}