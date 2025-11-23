package com.example.bustraveller.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bustraveller.data.local.converters.Converters
import com.example.bustraveller.data.local.dao.ParcelDao
import com.example.bustraveller.data.local.dao.VehicleDao
import com.example.bustraveller.data.local.entity.ParcelEntity
import com.example.bustraveller.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, ParcelEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TrackingDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun parcelDao(): ParcelDao
}

