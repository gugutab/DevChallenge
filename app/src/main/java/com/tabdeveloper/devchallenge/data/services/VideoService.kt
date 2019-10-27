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
                "Title",
//                "https://testdrive-archive.azurewebsites.net/Graphics/VideoFormatSupport/big_buck_bunny_trailer_480p_baseline.mp4",
                "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4",
                "https://miro.medium.com/max/3000/1*MI686k5sDQrISBM6L8pf5A.jpeg",
                "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"
            )
            val list =
                VideoListModel(listOf(videoModel, videoModel, videoModel, videoModel, videoModel))
            return Single.just(list)
                .delay(1, TimeUnit.SECONDS)
//                .timeout(4, TimeUnit.SECONDS)
        }
    }
}