package com.example.bustraveller.data.local.mapper

import com.example.bustraveller.data.local.entity.ParcelEntity
import com.example.bustraveller.data.local.entity.VehicleEntity
import com.example.bustraveller.data.model.TrackableItem
import com.google.android.gms.maps.model.LatLng

object EntityMapper {
    fun VehicleEntity.toDomainModel(): TrackableItem.Vehicle {
        return TrackableItem.Vehicle(
            id = id,
            name = name,
            currentLocation = LatLng(latitude, longitude),
            lastUpdateTime = lastUpdateTime,
            status = status,
            routeNumber = routeNumber,
            driverName = driverName,
            speed = speed,
            heading = heading,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation
        )
    }
    
    fun ParcelEntity.toDomainModel(): TrackableItem.Parcel {
        return TrackableItem.Parcel(
            id = id,
            name = name,
            currentLocation = LatLng(latitude, longitude),
            lastUpdateTime = lastUpdateTime,
            status = status,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            carrierName = carrierName
        )
    }
    
    fun TrackableItem.Vehicle.toEntity(): VehicleEntity {
        return VehicleEntity(
            id = id,
            name = name,
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            lastUpdateTime = lastUpdateTime,
            status = status,
            routeNumber = routeNumber,
            driverName = driverName,
            speed = speed,
            heading = heading,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation
        )
    }
    
    fun TrackableItem.Parcel.toEntity(): ParcelEntity {
        return ParcelEntity(
            id = id,
            name = name,
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            lastUpdateTime = lastUpdateTime,
            status = status,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            carrierName = carrierName
        )
    }
}

