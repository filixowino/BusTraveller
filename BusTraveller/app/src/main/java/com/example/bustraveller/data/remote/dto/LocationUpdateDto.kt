package com.example.bustraveller.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationUpdateDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("speed")
    val speed: Float? = null,
    @SerializedName("heading")
    val heading: Float? = null
)

