package com.tabdeveloper.devchallenge.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
import com.tabdeveloper.devchallenge.data.services.VideoService
import com.tabdeveloper.devchallenge.ui.player.PlayerActivity
import com.tabdeveloper.devchallenge.utils.DownloadHelper
import com.tabdeveloper.devchallenge.utils.dpToPixels
import kotlinx.android.synthetic.main.home_itemview.view.*
import timber.log.Timber
import kotlin.math.roundToInt

class HomeAdapter(
    val context: Context,
    val videoListModel: VideoListModel,
    val videoService: VideoService
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_itemview, parent, false)
        return VideoListViewHolder(v)
    }

    override fun getItemCount(): Int {
        return videoListModel.objects.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoListViewHolder).bind(
            videoListModel.objects[position],
            position,
            videoListModel,
            videoService
        )
    }

}

class VideoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        videoModel: VideoModel,
        position: Int,
        videoListModel: VideoListModel,
        videoService: VideoService
    ) {
        itemView.setOnClickListener {
            startActivity(
                itemView.context,
                PlayerActivity.newIntent(itemView.context, videoListModel, position),
                null
            )
        }
        itemView.home_itemview_title.text = videoModel.name
        Glide.with(itemView)
            .load(videoModel.image)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.home_placeholder))
            .apply(
                RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(4.dpToPixels(itemView.context).roundToInt())
                )
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.home_itemview_image)

        //Download
        DownloadHelper.getDownloadedVideoModel(itemView.context, videoModel)?.let {
            Timber.d("is downloaded!")
            itemView.home_itemview_download_button.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_offline_pin_black_24dp
                )
            )
            itemView.home_itemview_download_button.setOnClickListener {
                Toast.makeText(itemView.context, "Content downloaded", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            itemView.home_itemview_download_button.setOnClickListener {
                DownloadHelper.downloadVideoModel(itemView.context, videoModel, videoService)
                    .subscribe {
                        Timber.d("download DONE!")
                    }
            }
        }

    }
}