package com.example.timecapsule

data class HomeTimeCapsule(
    val id: Int,
    val title: String,
    val viewableAt: String,
    val latitude: Double,
    val longitude: Double,
    val daysLeft: Int
)
