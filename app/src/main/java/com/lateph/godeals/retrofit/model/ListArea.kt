package com.lateph.godeals.retrofit.model

object ListArea {
    data class Result(val name: String, val code: Int, val status: Int, val data: List<Data>)
    data class Data(val id: String, val name: String, val pointCoordinates: List<Coordinate>)
    data class Coordinate(val latitude: Double, val longitude: Double)

    data class Error(val name:String, val message: String, val data: ListError)
    data class ListError(val login: List<String>, val password: List<String>)
}