package com.tabdeveloper.devchallenge.data.model

import com.google.gson.annotations.SerializedName

data class VideoListModel(
    @SerializedName("objects") val name: List<VideoModel>
)