package com.tabdeveloper.devchallenge.ui.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
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
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        videoListModel = intent.getParcelableExtra(VIDEO_LIST_MODEL)
        currentPosition = intent.getIntExtra(SELECTED_POSITION, 0)
        videoModel = videoListModel?.objects?.get(currentPosition)

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

    fun setupButtons() {
        setupPlayPauseButton()
        if (!videoListModel?.objects.isNullOrEmpty()) {
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

    fun setupPlayPauseButton() {
        if (audioMediaPlayer != null && audioMediaPlayer!!.isPlaying) {
            activity_player_play_pause_button.setImageResource(R.drawable.ic_pause_circle_outline_black_72dp)
        } else {
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
                setupButtons()
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