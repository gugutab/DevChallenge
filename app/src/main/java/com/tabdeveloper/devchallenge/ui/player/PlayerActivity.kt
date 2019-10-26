package com.tabdeveloper.devchallenge.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoModel
import timber.log.Timber

class PlayerActivity : AppCompatActivity() {

    companion object {
        val VIDEO_MODEL = "video_model"

        fun newIntent(context: Context, videoModel: VideoModel): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(VIDEO_MODEL, videoModel)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val videoModel: VideoModel = intent.getParcelableExtra(VIDEO_MODEL)
        Timber.d("PlayerActivity: $videoModel")
    }
}
