package com.example.geoclock.model

import android.graphics.Bitmap
import android.os.Parcelable

data class Card(
    val cardId: String = "",
    val title: String = "",
    val userName: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "", // Add location field
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val note:String = "",
    val photo: String = "" //add photo field -> change to String
)