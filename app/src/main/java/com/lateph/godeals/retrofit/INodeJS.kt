package com.lateph.godeals.retrofit

import com.lateph.godeals.retrofit.model.ListArea
import com.lateph.godeals.retrofit.model.LoginModel
import io.reactivex.Observable
import retrofit2.http.*

interface INodeJS{
    @POST("login")
    fun loginUser(@Body params: LoginModel.Body): Observable<LoginModel.Result>

    @GET("list/area")
    fun listArea(): Observable<ListArea.Result>
}
