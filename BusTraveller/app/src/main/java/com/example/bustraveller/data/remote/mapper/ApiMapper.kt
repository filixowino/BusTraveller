package com.example.bustraveller.data.remote.mapper

import com.example.bustraveller.data.local.entity.ParcelEntity
import com.example.bustraveller.data.local.entity.VehicleEntity
import com.example.bustraveller.data.model.ParcelStatus
import com.example.bustraveller.data.model.VehicleStatus
import com.example.bustraveller.data.remote.dto.ParcelDto
import com.example.bustraveller.data.remote.dto.VehicleDto

object ApiMapper {
    fun VehicleDto.toEntity(): VehicleEntity {
        return VehicleEntity(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            lastUpdateTime = lastUpdateTime,
            status = VehicleStatus.valueOf(status),
            routeNumber = routeNumber,
            driverName = driverName,
            speed = speed,
            heading = heading,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation
        )
    }
    
    fun ParcelDto.toEntity(): ParcelEntity {
        return ParcelEntity(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            lastUpdateTime = lastUpdateTime,
            status = ParcelStatus.valueOf(status),
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            carrierName = carrierName
        )
    }
    
    fun VehicleEntity.toDto(): VehicleDto {
        return VehicleDto(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            lastUpdateTime = lastUpdateTime,
            status = status.name,
            routeNumber = routeNumber,
            driverName = driverName,
            speed = speed,
            heading = heading,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation
        )
    }
    
    fun ParcelEntity.toDto(): ParcelDto {
        return ParcelDto(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            lastUpdateTime = lastUpdateTime,
            status = status.name,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            carrierName = carrierName
        )
    }
}

