package com.tabdeveloper.devchallenge.data.services

import com.tabdeveloper.devchallenge.application.AppConfiguration
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface VideoService {
    @GET("assets.json")
    fun getVideoModelList(
        @Query("alt") media: String = AppConfiguration.alt,
        @Query("token") token: String = AppConfiguration.token
    ): Single<VideoListModel>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Observable<Response<ResponseBody>>

    companion object {
        fun getVideoModelListMock(): Single<VideoListModel> {
            val videoModel = VideoModel(
                "Loop Video",
                "https://ak3.picdn.net/shutterstock/videos/1011845243/preview/stock-footage--d-render-abstract-seamless-background-looped-animation-fluorescent-ultraviolet-light-glowing.mp4",
                "https://cdn.pixabay.com/photo/2019/10/23/16/36/black-4572125__340.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel1 = VideoModel(
                "Big Buck Bunny (240p)",
                "https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_5mb.mp4",
                "https://cdn.pixabay.com/photo/2016/11/09/16/24/virus-1812092__340.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel2 = VideoModel(
                "Big Buck Bunny (480p)",
                "https://testdrive-archive.azurewebsites.net/Graphics/VideoFormatSupport/big_buck_bunny_trailer_480p_baseline.mp4",
                "https://cdn.pixabay.com/photo/2014/02/27/16/09/microscope-275984__340.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel3 = VideoModel(
                "Big Buck Bunny (720p)",
                "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "https://cdn.pixabay.com/photo/2017/06/13/13/49/indoor-2398938__340.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )

            val list =
                VideoListModel(listOf(videoModel, videoModel1, videoModel2, videoModel3))
            return Single.just(list)
                .delay(1, TimeUnit.SECONDS)
//                .timeout(4, TimeUnit.SECONDS)
        }
    }
}