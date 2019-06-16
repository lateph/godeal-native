package com.lateph.godeals.retrofit.model

object LoginModel {
    data class Result(val name: String, val code: Int, val status: Int, val data: Data)
    data class Data(val name: String, val mobileNumber: String, val email: String, val status: String, val verified: String, val device: Device)
    data class Device(val accessToken: String)

    data class Body(val login: String, val password: String)
    data class Error(val name:String, val message: String, val data: ListError)
    data class ListError(val login: List<String>, val password: List<String>)
}