package com.tabdeveloper.devchallenge.ui.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoModel
import kotlinx.android.synthetic.main.activity_player.*
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


    private var audioMediaPlayer: MediaPlayer? = null
    private var videoModel: VideoModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        videoModel = intent.getParcelableExtra(VIDEO_MODEL)

        activity_player_play_pause_button.setOnClickListener {
            if (audioMediaPlayer != null && audioMediaPlayer!!.isPlaying) {
                audioMediaPlayer?.pause()
                activity_player_videoview.pause()
            } else {
                audioMediaPlayer?.start()
                activity_player_videoview.start()
            }
            setupPlayPauseButton()
        }
    }

    fun setupPlayPauseButton(){
        if (audioMediaPlayer != null && audioMediaPlayer!!.isPlaying) {
            activity_player_play_pause_button.setImageResource(R.drawable.ic_pause_circle_outline_black_72dp)
        } else{
            activity_player_play_pause_button.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
        }
    }

    override fun onResume() {
        super.onResume()
        videoModel?.let {
            // audio
            audioMediaPlayer = MediaPlayer.create(this, Uri.parse(it.audio))
            audioMediaPlayer?.setOnPreparedListener {
                Timber.d("prepared")
                it.start()
                activity_player_play_pause_button.isVisible = true
            }
            audioMediaPlayer?.setOnCompletionListener {
                activity_player_videoview.stopPlayback()
            }
            // video
            activity_player_videoview.setVideoPath(it.video)
            activity_player_videoview.start()
            activity_player_videoview.setOnPreparedListener {
                it.isLooping = true
            }
        }
    }

    override fun onPause() {
        audioMediaPlayer?.pause()
        audioMediaPlayer?.release()
        super.onPause()
    }
}