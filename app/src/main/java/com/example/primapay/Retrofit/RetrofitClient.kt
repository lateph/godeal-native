package com.example.primapay.Retrofit

import android.content.Context
import com.example.primapay.helper.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RertrofitClient{
    companion object {
        fun create(context: Context):Retrofit{
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val preferencesHelper = PreferencesHelper(context)

                // Request customization: add request headers
                val requestBuilder = original.newBuilder()
                    .header("Authorization", preferencesHelper.accessToken) // <-- this is the important line

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            return Retrofit.Builder().baseUrl("http://159.89.205.235:4043/")
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