package com.tabdeveloper.devchallenge.ui.player

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
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
    private var exoplayer: SimpleExoPlayer? = null
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
        setContentView(com.tabdeveloper.devchallenge.R.layout.activity_player)
        videoListModel = intent.getParcelableExtra(VIDEO_LIST_MODEL)
        exoplayer = ExoPlayerFactory.newSimpleInstance(this)
        activity_player_videoview.player = exoplayer
        activity_player_videoview.hideController()

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
                exoplayer?.playWhenReady = false
            } else {
                audioMediaPlayer?.start()
                exoplayer?.playWhenReady = true
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
                    com.tabdeveloper.devchallenge.R.color.black
                )
            )
            activity_player_next_button.setColorFilter(
                ContextCompat.getColor(
                    this,
                    com.tabdeveloper.devchallenge.R.color.black
                )
            )
            when (currentPosition) {
                0 -> {
                    activity_player_previous_button.isEnabled = false
                    activity_player_previous_button.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            com.tabdeveloper.devchallenge.R.color.gray
                        )
                    )
                }
                ((videoListModel?.objects?.size ?: 0) - 1) -> {
                    activity_player_next_button.isEnabled = false
                    activity_player_next_button.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            com.tabdeveloper.devchallenge.R.color.gray
                        )
                    )
                }
            }
        }
    }

    private fun setupPlayPauseButton() {
        if (audioMediaPlayer != null && audioMediaPlayer!!.isPlaying) {
            activity_player_play_pause_button.setImageResource(com.tabdeveloper.devchallenge.R.drawable.ic_pause_circle_outline_black_72dp)
        } else {
            activity_player_play_pause_button.setImageResource(com.tabdeveloper.devchallenge.R.drawable.ic_play_circle_outline_black_72dp)
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
        exoplayer?.stop(true)
        //
        videoModel?.let {
            activity_player_loading.isVisible = true

            //TODO check artwork bugs
//            activity_player_videoview.defaultArtwork =
//                ContextCompat.getDrawable(this, R.drawable.dark_background)
//            activity_player_videoview.setBackgroundResource(com.tabdeveloper.devchallenge.R.drawable.dark_background)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                Glide.with(this).asDrawable().load(videoModelDownloaded?.image ?: it.image)
//                    .into(object : CustomTarget<Drawable>() {
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                            //do nothing
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable,
//                            transition: Transition<in Drawable>?
//                        ) {
//                            activity_player_videoview.defaultArtwork = resource
//                        }
//                    })
//            }

            // video
            val dataSourceFactory = DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, "DevChallenge")
            )
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoModelDownloaded?.video ?: it.video))
            // Prepare the player with the source.
            exoplayer?.playWhenReady = false
            exoplayer?.prepare(videoSource)
            exoplayer?.volume = 0f
            exoplayer?.repeatMode = Player.REPEAT_MODE_ALL

            exoplayer?.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> activity_player_loading.isVisible = true
                        Player.STATE_READY -> {
                            if (!videoPrepared) {
                                videoPrepared = true
                            } else{
                                activity_player_loading.isVisible = false
                            }
                        }
                    }
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                }
            })
            // audio
            audioMediaPlayer = MediaPlayer()
            audioMediaPlayer?.setDataSource(videoModelDownloaded?.audio ?: it.audio)
            audioMediaPlayer?.prepareAsync()
            audioMediaPlayer?.setOnPreparedListener {
                Timber.d("audio prepared")
                audioPrepared = true
            }
            audioMediaPlayer?.setOnCompletionListener {
                exoplayer?.stop(true)
                activity_player_play_pause_button.setImageResource(com.tabdeveloper.devchallenge.R.drawable.ic_replay_black_72dp)
                activity_player_play_pause_button.setOnClickListener {
                    changeCurrentPosition(currentPosition)
                }
            }
        }
    }

    fun startPlayback() {
        Timber.d("startPlayback")
        activity_player_videoview.setBackgroundResource(0)
        exoplayer?.playWhenReady = true
        activity_player_play_pause_button.isVisible = true
        setupButtons()
        activity_player_loading.isVisible = false
        audioMediaPlayer?.start()
        setupPlayPauseButton()

    }

    override fun onPause() {
        audioMediaPlayer?.stop()
        audioMediaPlayer?.reset()
        audioMediaPlayer?.release()
        exoplayer?.stop(true)
        super.onPause()
    }
}