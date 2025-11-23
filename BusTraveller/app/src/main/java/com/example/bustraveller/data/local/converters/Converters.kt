package com.example.bustraveller.data.local.converters

import androidx.room.TypeConverter
import com.example.bustraveller.data.model.ParcelStatus
import com.example.bustraveller.data.model.VehicleStatus

class Converters {
    @TypeConverter
    fun fromVehicleStatus(status: VehicleStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toVehicleStatus(status: String): VehicleStatus {
        return VehicleStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromParcelStatus(status: ParcelStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toParcelStatus(status: String): ParcelStatus {
        return ParcelStatus.valueOf(status)
    }
}

