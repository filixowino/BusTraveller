package com.example.bustraveller.data.local.dao

import androidx.room.*
import com.example.bustraveller.data.local.entity.VehicleEntity
import com.example.bustraveller.data.model.VehicleStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>
    
    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: String): VehicleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)
    
    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)
    
    @Query("UPDATE vehicles SET latitude = :latitude, longitude = :longitude, lastUpdateTime = :updateTime, speed = :speed, heading = :heading WHERE id = :id")
    suspend fun updateLocation(id: String, latitude: Double, longitude: Double, updateTime: Long, speed: Float, heading: Float)
    
    @Query("UPDATE vehicles SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: VehicleStatus)
    
    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)
    
    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicleById(id: String)
}

