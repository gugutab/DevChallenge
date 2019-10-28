package com.tabdeveloper.devchallenge.utils

import android.content.Context

fun Int.dpToPixels(context: Context): Float {
    return this * (context.resources.displayMetrics.density)
}

fun String.getIntFromString(): Int {
    var res = 0
    this.toCharArray().forEach {
        res = res * 31 + it.hashCode()
    }
    return res
}