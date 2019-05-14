package com.example.primapay.Retrofit.model

object LoginModel {
    data class Result(val accessToken: String, val refreshToken: String)
    data class ResultUsers(val _id: String, val nameId: String, val email: String, val imgProfile: String, val imgBackground: String)
    data class SearchInfo(val totalhits: Int)
}