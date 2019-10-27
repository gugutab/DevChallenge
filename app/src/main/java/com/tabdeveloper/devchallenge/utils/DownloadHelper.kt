package com.tabdeveloper.devchallenge.utils

import android.content.Context
import com.tabdeveloper.devchallenge.data.model.VideoModel
import com.tabdeveloper.devchallenge.data.services.VideoService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class DownloadHelper {
    companion object {

        fun getDownloadedVideoModel(context: Context, videoModel: VideoModel): VideoModel? {
            val folder = File(context.filesDir, "${videoModel.name.getIntFromString()}/")
            if (folder.exists()) {
                return VideoModel(videoModel.name, folder.path+"/video.mp4", folder.path+"/image.jpg", folder.path+"/audio.mp3")
            }
            return null
        }

        fun downloadVideoModel(
            context: Context,
            videoModel: VideoModel,
            videoService: VideoService
        ): Observable<File> {
            //create folder
            val folder = File(context.filesDir, "${videoModel.name.getIntFromString()}/")
            if (!folder.exists()) {
                folder.mkdirs()
            }
            //
            return Observable.concat<File>(
                downloadFile(videoService, videoModel.audio, folder, "audio.mp3"),
                downloadFile(videoService, videoModel.image, folder, "image.jpg"),
                downloadFile(videoService, videoModel.video, folder, "video.mp4")
            )

        }

        fun downloadFile(
            videoService: VideoService,
            url: String,
            path: File,
            fileName: String
        ): Observable<File> {
            return videoService.downloadFile(url)
                .flatMap(io.reactivex.functions.Function<Response<ResponseBody>, Observable<File>> { t ->
                    try {
                        val file = File(path, fileName)
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.write(t.body()?.bytes())
                        fileOutputStream.close()
                        return@Function Observable.just(file)
                    } catch (ex: Exception) {
                        throw ex
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { Timber.d("download start: $fileName") }
                .doOnComplete { Timber.d("download done: $fileName") }
        }
    }
}