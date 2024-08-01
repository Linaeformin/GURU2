package com.example.timecapsule

data class ViewableCapsule(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val fileName: String,
    val viewableAt: String,
    val latitude: Double,
    val longitude: Double
)
