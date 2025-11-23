package com.example.bustraveller.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VehicleDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("lastUpdateTime")
    val lastUpdateTime: Long,
    @SerializedName("status")
    val status: String,
    @SerializedName("routeNumber")
    val routeNumber: String,
    @SerializedName("driverName")
    val driverName: String? = null,
    @SerializedName("speed")
    val speed: Float = 0f,
    @SerializedName("heading")
    val heading: Float = 0f,
    @SerializedName("departureLocation")
    val departureLocation: String? = null,
    @SerializedName("arrivalLocation")
    val arrivalLocation: String? = null
)

