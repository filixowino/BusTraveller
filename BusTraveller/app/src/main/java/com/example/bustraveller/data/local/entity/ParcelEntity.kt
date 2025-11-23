package com.example.bustraveller.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bustraveller.data.model.ParcelStatus

@Entity(tableName = "parcels")
data class ParcelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdateTime: Long,
    val status: ParcelStatus,
    val trackingNumber: String,
    val estimatedDelivery: Long? = null,
    val carrierName: String? = null
)

