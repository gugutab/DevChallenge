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
                "Abstract Glowing Loop Video",
                "https://ak3.picdn.net/shutterstock/videos/1011845243/preview/stock-footage--d-render-abstract-seamless-background-looped-animation-fluorescent-ultraviolet-light-glowing.mp4",
                "https://ak7.picdn.net/shutterstock/videos/1035100667/thumb/1.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel1 = VideoModel(
                "Neon Glow Loop Video",
                "https://ak6.picdn.net/shutterstock/videos/1027713866/preview/stock-footage-tv-series-colorful-neon-glow-color-moving-seamless-art-loop-background-abstract-motion-screen.mp4",
                "https://ak6.picdn.net/shutterstock/videos/1027713866/thumb/10.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel2 = VideoModel(
                "White Steam Loop Video",
                "https://ak9.picdn.net/shutterstock/videos/1024315289/preview/stock-footage-white-steam-spins-and-rises-from-the-pan-white-smoke-rises-from-a-large-pot-which-is-located.mp4",
                "https://ak9.picdn.net/shutterstock/videos/1024315289/thumb/7.jpg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val videoModel3 = VideoModel(
                "Rain Loop Video",
                "https://ak3.picdn.net/shutterstock/videos/1028275673/preview/stock-footage--k-loop-rain-drops-falling-alpha-real-rain-high-quality-slow-rain-thunder-speedy-night.mp4",
                "https://ak3.picdn.net/shutterstock/videos/1028275673/thumb/1.jpg",
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