package com.example.bustraveller.data.model

import com.google.android.gms.maps.model.LatLng

sealed class TrackableItem {
    abstract val id: String
    abstract val name: String
    abstract val currentLocation: LatLng
    abstract val lastUpdateTime: Long
    
    data class Vehicle(
        override val id: String,
        override val name: String,
        override val currentLocation: LatLng,
        override val lastUpdateTime: Long,
        val status: VehicleStatus,
        val routeNumber: String,
        val driverName: String? = null,
        val speed: Float = 0f,
        val heading: Float = 0f,
        val departureLocation: String? = null,
        val arrivalLocation: String? = null
    ) : TrackableItem()
    
    data class Parcel(
        override val id: String,
        override val name: String,
        override val currentLocation: LatLng,
        override val lastUpdateTime: Long,
        val status: ParcelStatus,
        val trackingNumber: String,
        val estimatedDelivery: Long? = null,
        val carrierName: String? = null
    ) : TrackableItem()
}

enum class VehicleStatus {
    DEPARTED,
    ARRIVED
}

enum class ParcelStatus {
    IN_TRANSIT,
    BEING_PARKED,
    READY_FOR_DELIVERY,
    ARRIVED
}

