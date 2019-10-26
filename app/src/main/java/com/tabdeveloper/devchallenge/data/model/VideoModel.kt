package com.tabdeveloper.devchallenge.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoModel(
    @SerializedName("name") val name: String,
    @SerializedName("bg") val video: String,
    @SerializedName("im") val image: String,
    @SerializedName("sg") val audio: String
) : Parcelable