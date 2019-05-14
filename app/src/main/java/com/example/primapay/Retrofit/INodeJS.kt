package com.example.primapay.Retrofit

import com.example.primapay.Retrofit.model.LoginModel
import io.reactivex.Observable
import retrofit2.http.*

interface INodeJS{
    @POST("authentication")
    @FormUrlEncoded
    fun loginUser(@Field("email") email:String,
                  @Field("password") password:String,
                  @Field("strategy") strategy:String,
                  @Field("remember_me") rememberMe:Boolean): Observable<LoginModel.Result>

    @GET("users/{id}")
    fun getUser(@Path("id") id:String): Observable<LoginModel.ResultUsers>
}
