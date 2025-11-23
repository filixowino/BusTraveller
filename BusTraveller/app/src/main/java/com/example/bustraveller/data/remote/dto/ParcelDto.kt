package com.example.bustraveller.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ParcelDto(
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
    @SerializedName("trackingNumber")
    val trackingNumber: String,
    @SerializedName("estimatedDelivery")
    val estimatedDelivery: Long? = null,
    @SerializedName("carrierName")
    val carrierName: String? = null
)

