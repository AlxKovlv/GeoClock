package com.example.geoclock.model

data class Card(
    val cardId: String = "",
    val title: String = "",
    val userName: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "", // Add location field
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
){}