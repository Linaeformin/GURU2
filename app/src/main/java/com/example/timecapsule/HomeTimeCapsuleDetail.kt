package com.example.timecapsule

data class HomeTimeCapsuleDetail(
    val id: Int,
    val title: String,
    val viewableAt: String,
    val latitude: Double,
    val longitude: Double,
    val daysLeft: Int
)
