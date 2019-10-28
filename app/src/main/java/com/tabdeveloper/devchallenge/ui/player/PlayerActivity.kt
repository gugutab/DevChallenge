package com.tabdeveloper.devchallenge.ui.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
import com.tabdeveloper.devchallenge.utils.DownloadHelper
import kotlinx.android.synthetic.main.activity_player.*
import timber.log.Timber

class PlayerActivity : AppCompatActivity() {

    companion object {
        val VIDEO_LIST_MODEL = "video_list_model"
        val SELECTED_POSITION = "selected_position"

        fun newIntent(
            context: Context, videoListModel: VideoListModel, selectedPosition: Int
        ): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(VIDEO_LIST_MODEL, videoListModel)
            intent.putExtra(SELECTED_POSITION, selectedPosition)
            return intent
        }
    }


    private var audioMediaPlayer: MediaPlayer? = null
    private var videoListModel: VideoListModel? = null
    private var videoModel: VideoModel? = null
    private var videoModelDownloaded: VideoModel? = null
    private var currentPosition = 0
    var audioPrepared: Boolean = false
        set(value) {
            field = value
            if (value && videoPrepared) {
                startPlayback()
            }
        }
    var videoPrepared: Boolean = false
        set(value) {
            field = value
            if (value && audioPrepared) {
                startPlayback()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        videoListModel = intent.getParcelableExtra(VIDEO_LIST_MODEL)
    }

    override fun onResume() {
        changeCurrentPosition(intent.getIntExtra(SELECTED_POSITION, 0))
        super.onResume()
    }

    private fun changeCurrentPosition(newPosition: Int) {
        currentPosition = newPosition
        videoModel = videoListModel?.objects?.get(currentPosition)
        DownloadHelper.getDownloadedVideoModel(this, videoModel!!)?.let {
            Timber.d("is downloaded!")
            videoModelDownloaded = it
        } ?: run { videoModelDownloaded = null }
        this.title = videoModel?.name
        preparePlayback()
    }

    private fun setupButtons() {
        setupPlayPauseButton()
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
        if (!videoListModel?.objects.isNullOrEmpty()) {
            activity_player_previous_button.setOnClickListener {
                if (currentPosition == 0) {
                    changeCurrentPosition((videoListModel?.objects?.size ?: 0) - 1)
                } else {
                    changeCurrentPosition(currentPosition - 1)
                }
            }
            activity_player_next_button.setOnClickListener {
                if (currentPosition == (videoListModel?.objects?.size ?: 0) - 1) {
                    changeCurrentPosition(0)
                } else {
                    changeCurrentPosition(currentPosition + 1)
                }
            }

            activity_player_previous_button.isVisible = true
            activity_player_next_button.isVisible = true

            activity_player_previous_button.isEnabled = true
            activity_player_next_button.isEnabled = true

            activity_player_previous_button.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            activity_player_next_button.setColorFilter(ContextCompat.getColor(this, R.color.black))
            when (currentPosition) {
                0 -> {
                    activity_player_previous_button.isEnabled = false
                    activity_player_previous_button.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.gray
                        )
                    )
                }
                ((videoListModel?.objects?.size ?: 0) - 1) -> {
                    activity_player_next_button.isEnabled = false
                    activity_player_next_button.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.gray
                        )
                    )
                }
            }
        }
    }

    private fun setupPlayPauseButton() {
        if (audioMediaPlayer != null && audioMediaPlayer!!.isPlaying) {
            activity_player_play_pause_button.setImageResource(R.drawable.ic_pause_circle_outline_black_72dp)
        } else {
            activity_player_play_pause_button.setImageResource(R.drawable.ic_play_circle_outline_black_72dp)
        }
    }

    private fun preparePlayback() {
        // clear
        videoPrepared = false
        audioPrepared = false
        activity_player_play_pause_button.isVisible = false
        activity_player_next_button.isVisible = false
        activity_player_previous_button.isVisible = false
        audioMediaPlayer?.release()
        activity_player_videoview.stopPlayback()
        //
        videoModel?.let {
            activity_player_loading.isVisible = true
            activity_player_videoview.setBackgroundResource(R.drawable.dark_background)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Glide.with(this).asDrawable().load(videoModelDownloaded?.image ?: it.image)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            //do nothing
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            activity_player_videoview.background = resource
                        }
                    })
            }
            // video
            activity_player_videoview.setVideoPath(videoModelDownloaded?.video ?: it.video)
            activity_player_videoview.setOnPreparedListener {
                Timber.d("video prepared")
                videoPrepared = true
                it.isLooping = true
                it.setVolume(0f, 0f)
                it.setOnInfoListener { mp, what, extra ->
                    when (what) {
                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            Timber.d("setOnInfoListener MEDIA_INFO_BUFFERING_START")
                            activity_player_loading.isVisible = true
                        }
                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            Timber.d("setOnInfoListener MEDIA_INFO_BUFFERING_END")
                            activity_player_loading.isVisible = false
                        }
                    }
                    true
                }
            }
            // audio
            audioMediaPlayer = MediaPlayer()
            audioMediaPlayer?.setDataSource(videoModelDownloaded?.audio ?: it.audio)
            audioMediaPlayer?.prepareAsync()
            audioMediaPlayer?.setOnPreparedListener {
                Timber.d("audio prepared")
                audioPrepared = true
            }
            audioMediaPlayer?.setOnCompletionListener {
                activity_player_videoview.stopPlayback()
                activity_player_play_pause_button.setImageResource(R.drawable.ic_replay_black_72dp)
                activity_player_play_pause_button.setOnClickListener {
                    changeCurrentPosition(currentPosition)
                }
            }
        }
    }

    fun startPlayback() {
        Timber.d("startPlayback")
        activity_player_videoview.setBackgroundResource(0)
        activity_player_videoview.start()
        activity_player_play_pause_button.isVisible = true
        setupButtons()
        setupPlayPauseButton()
        activity_player_loading.isVisible = false
        audioMediaPlayer?.start()
    }

    override fun onPause() {
        audioMediaPlayer?.stop()
        audioMediaPlayer?.reset()
        audioMediaPlayer?.release()
        activity_player_videoview?.stopPlayback()
        super.onPause()
    }
}