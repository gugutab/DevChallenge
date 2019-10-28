package com.tabdeveloper.devchallenge.ui.home

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.Module
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.services.VideoService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber

class HomeActivity : AppCompatActivity() {
    val videoService = Module.createService()
    var useMock: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        activity_home_mock_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            Timber.d("switch $isChecked")
            useMock = isChecked
            loadContent()
        }
    }

    fun getContentObservable(): Single<VideoListModel> {
        return if (useMock) {
            VideoService.getVideoModelListMock()
        } else {
            videoService.getVideoModelList()
        }
    }

    fun loadContent() {
        getContentObservable()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                activity_home_loading.isVisible = true
                activity_home_swipe_layout.isVisible = false
                activity_home_error.isVisible = false
            }
            .subscribe({
                activity_home_recycler.layoutManager = LinearLayoutManager(this)
                activity_home_recycler.adapter = HomeAdapter(it, videoService)
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
