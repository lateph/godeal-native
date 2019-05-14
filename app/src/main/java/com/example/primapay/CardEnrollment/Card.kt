package com.example.primapay.CardEnrollment
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Card(val cardId: String, val sourceId: String) : Parcelable