package com.tabdeveloper.devchallenge.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoListModel(
    @SerializedName("objects") val objects: List<VideoModel>
) : Parcelable