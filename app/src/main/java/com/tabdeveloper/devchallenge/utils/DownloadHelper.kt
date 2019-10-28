package com.tabdeveloper.devchallenge.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.tabdeveloper.devchallenge.R
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

        val videoFileName: String = "video"
        val audioFileName: String = "audio"
        val imageFileName: String = "image"
        val downloadCompleteFileName: String = "download_complete"

        fun getDownloadedVideoModel(context: Context, videoModel: VideoModel): VideoModel? {
            val folder = File(
                context.filesDir,
                "${videoModel.name.getIntFromString()}/"
            )
            if (folder.exists()) {
                val completeFile =
                    File(folder.path, "/$downloadCompleteFileName")
                if (completeFile.exists()) {
                    return VideoModel(
                        videoModel.name,
                        "${folder.path}/$videoFileName",
                        "${folder.path}/$imageFileName",
                        "${folder.path}/$audioFileName"
                    )
                }
            }
            return null
        }

        fun downloadVideoModel(
            context: Context,
            videoModel: VideoModel,
            videoService: VideoService
        ): Observable<File> {
            val downloadDialog = displayDownloadDialog(context)
            //create folder
            val folder = File(context.filesDir, "${videoModel.name.getIntFromString()}/")
            if (!folder.exists()) {
                folder.mkdirs()
            }
            //
            val observable = Observable.concat(
                downloadFile(videoService, videoModel.audio, folder, audioFileName),
                downloadFile(videoService, videoModel.image, folder, imageFileName),
                downloadFile(videoService, videoModel.video, folder, videoFileName)
            )
                .doOnSubscribe { disposable ->
                    downloadDialog.setOnDismissListener {
                        if(!disposable.isDisposed){
                            disposable.dispose()
                        }
                    }
                    downloadDialog.show()
                }
                .doOnComplete {
                    downloadDialog.dismiss()
                    val completeFile = File(folder, downloadCompleteFileName)
                    val stream = FileOutputStream(completeFile)
                    stream.use {
                        it.write("done".toByteArray())
                    }
                }
            return observable
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
                .doOnSubscribe {
                    Timber.d("download start: $fileName")
                }
                .doOnComplete { Timber.d("download done: $fileName") }
        }

        private fun displayDownloadDialog(context: Context): AlertDialog {
            val customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_download, null)

            return AlertDialog.Builder(context)
                .setTitle("Downloading")
                .setCancelable(false)
                .setView(customLayout)
                .setNeutralButton(R.string.download_dialog_cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
//                .setView(createdView)
//                .setPositiveButton("Save") { _, _ ->
//                    val title = titleField.text.toString()
//                    val description = descriptionField.text.toString()
//                    val note = ContactsContract.CommonDataKinds.Note(
//                        title = title,
//                        description = description
//                    )
//                    //logo antes de executar o insert, a HOF é executada
//                    preExecute()
//                    NoteWebClient().insert(note, {
//                        created(it)
//                    }, {
//                        Toast.makeText(context, "Falha ao salvar nota", Toast.LENGTH_LONG).show()
//                    })
//                }
//                .show()
        }
    }
}