package com.example.bustraveller.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bustraveller.data.model.VehicleStatus

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdateTime: Long,
    val status: VehicleStatus,
    val routeNumber: String,
    val driverName: String? = null,
    val speed: Float = 0f,
    val heading: Float = 0f,
    val departureLocation: String? = null,
    val arrivalLocation: String? = null
)

