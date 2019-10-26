package com.tabdeveloper.devchallenge.utils

import android.content.Context

fun Int.dpToPixels(context: Context): Float {
    return this * (context.resources.displayMetrics.density)
}