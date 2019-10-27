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
import kotlinx.android.synthetic.main.activity_player.*

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
    private var currentPosition = 0

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
        this.title = videoModel?.name
        startPlayback()
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

    private fun startPlayback() {
        // clear
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
                Glide.with(this).asDrawable().load(it.image)
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
            activity_player_videoview.setVideoPath(it.video)
            activity_player_videoview.setOnPreparedListener {
                it.isLooping = true
                it.setVolume(0f, 0f)
            }
            // audio
            audioMediaPlayer = MediaPlayer()
            audioMediaPlayer?.setDataSource(it.audio)
            audioMediaPlayer?.prepareAsync()
            audioMediaPlayer?.setOnPreparedListener {
                it.start()
                activity_player_play_pause_button.isVisible = true
                setupButtons()
                setupPlayPauseButton()
                activity_player_videoview.setBackgroundResource(0)
                activity_player_videoview.start()
                activity_player_loading.isVisible = false
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

    override fun onPause() {
        audioMediaPlayer?.stop()
        audioMediaPlayer?.reset()
        audioMediaPlayer?.release()
        activity_player_videoview?.stopPlayback()
        super.onPause()
    }
}