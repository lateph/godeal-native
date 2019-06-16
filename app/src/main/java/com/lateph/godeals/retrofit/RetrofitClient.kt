package com.lateph.godeals.retrofit

import android.content.Context
import android.util.Log
import com.lateph.godeals.helper.PreferencesHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RertrofitClient{
    companion object {
        fun create(context: Context):Retrofit{
            val httpClient = OkHttpClient.Builder()
            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient.addInterceptor(interceptor)
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val preferencesHelper = PreferencesHelper(context)

                // Request customization: add request headers
                 Log.d("mytest", preferencesHelper.accessToken)
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer "+preferencesHelper.accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-App-key", "GoDealsDevelopmentAppKey")
                    .addHeader("X-App-secret","GoDealsDevelopmentAppSecret")// <-- this is the important line
                    .addHeader("X-Device-identifier","android")// <-- this is the important line

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            return Retrofit.Builder().baseUrl("http://api.godeals.server-development.net/")
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
    val instance:Retrofit
        get(){
            return Retrofit.Builder().baseUrl("http://159.89.205.235:4043/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }


}