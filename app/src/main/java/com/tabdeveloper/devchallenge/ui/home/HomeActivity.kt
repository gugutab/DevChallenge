package com.tabdeveloper.devchallenge.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber

class HomeActivity : AppCompatActivity() {
    val videoService = Module.createService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        videoService.getVideoModelList()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                activity_main_recycler.layoutManager = LinearLayoutManager(this)
                activity_main_recycler.adapter = HomeAdapter(this, it)
            },
                { throwable: Throwable ->
                    Timber.e(throwable)
                })
    }
}
