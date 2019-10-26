package com.tabdeveloper.devchallenge.data.model

import com.google.gson.annotations.SerializedName

data class VideoModel(
    @SerializedName("name") val name: String,
    @SerializedName("bg") val video: String,
    @SerializedName("im") val image: String,
    @SerializedName("sg") val audio: String
)