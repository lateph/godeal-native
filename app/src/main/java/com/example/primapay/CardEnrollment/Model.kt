package com.example.primapay.CardEnrollment

object Model {
    data class Card(val accessToken: String, val refreshToken: String)
    data class CardType(val id: String, val text: String)
    fun getListCardType():ArrayList<CardType> {
        val ars = ArrayList<CardType>()
        ars.add(CardType("debit", "Debit Card"))
        ars.add(CardType("cc", "Credit Card"))
        return ars
    }
}