package com.tabdeveloper.devchallenge.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    val videoService = Module.createService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoService.getVideoModelList()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("log", "opa ${it}")
            },
                { throwable: Throwable ->
                    Timber.e(throwable)
                })
    }
}
