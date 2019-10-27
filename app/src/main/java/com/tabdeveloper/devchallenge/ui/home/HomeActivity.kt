package com.tabdeveloper.devchallenge.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.Module
import com.tabdeveloper.devchallenge.data.services.VideoService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class HomeActivity : AppCompatActivity() {
    val videoService = Module.createService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loadContent()
        activity_home_swipe_layout.setOnRefreshListener {
            activity_home_swipe_layout.isRefreshing = false
            loadContent()
        }
        activity_home_error_reload.setOnClickListener {
            loadContent()
        }
    }

    fun loadContent() {
        videoService.downloadFile("https://miro.medium.com/max/3000/1*MI686k5sDQrISBM6L8pf5A.jpeg")
            .flatMap(Function<Response<ResponseBody>, Observable<File>>() { t ->
                try {
                    val file = File(this.filesDir, "image.jpg")
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(t.body()?.bytes())
                    fileOutputStream.close()
                    return@Function Observable.just(file)
                } catch (ex: Exception) {
                    return@Function Observable.just(null)
                }
            })
            .doOnSubscribe { Timber.d("download start") }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("download complete")
            }
//                videoService.getVideoModelList()
        VideoService.getVideoModelListMock() // todo revert to actual data
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                activity_home_loading.isVisible = true
                activity_home_swipe_layout.isVisible = false
                activity_home_error.isVisible = false
            }
            .subscribe({
                activity_home_recycler.layoutManager = LinearLayoutManager(this)
                activity_home_recycler.adapter = HomeAdapter(this, it)
                activity_home_loading.isVisible = false
                activity_home_swipe_layout.isVisible = true
            },
                { throwable: Throwable ->
                    activity_home_error.isVisible = true
                    activity_home_loading.isVisible = false
                    Timber.e(throwable)
                })
    }
}
