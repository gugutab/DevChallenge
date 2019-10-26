package com.tabdeveloper.devchallenge.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tabdeveloper.devchallenge.application.AppConfiguration
import com.tabdeveloper.devchallenge.data.services.VideoService
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

object Module {
    private val defaultGsonBuilder: Gson
        get() = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
    private val builder = okhttp3.OkHttpClient.Builder().addInterceptor(LogInterceptor()).build()

    fun createService(): VideoService = Retrofit.Builder()
        .client(builder)
        .baseUrl(AppConfiguration.databaseUrl)
        .addConverterFactory(GsonConverterFactory.create(defaultGsonBuilder))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(VideoService::class.java)
}

class LogInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Timber.v("Sending request [%s]: %s", request.method(), request.url())
        val response = chain.proceed(request)
        Timber.v("Received response (%s) for [%s] %s", response.code(), response.request().method(), response.request().url())
        return response
    }

}
