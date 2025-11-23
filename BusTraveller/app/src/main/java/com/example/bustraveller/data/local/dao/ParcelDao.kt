package com.example.bustraveller.data.local.dao

import androidx.room.*
import com.example.bustraveller.data.local.entity.ParcelEntity
import com.example.bustraveller.data.model.ParcelStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelDao {
    @Query("SELECT * FROM parcels")
    fun getAllParcels(): Flow<List<ParcelEntity>>
    
    @Query("SELECT * FROM parcels WHERE id = :id")
    suspend fun getParcelById(id: String): ParcelEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParcel(parcel: ParcelEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParcels(parcels: List<ParcelEntity>)
    
    @Update
    suspend fun updateParcel(parcel: ParcelEntity)
    
    @Query("UPDATE parcels SET latitude = :latitude, longitude = :longitude, lastUpdateTime = :updateTime WHERE id = :id")
    suspend fun updateLocation(id: String, latitude: Double, longitude: Double, updateTime: Long)
    
    @Query("UPDATE parcels SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: ParcelStatus)
    
    @Delete
    suspend fun deleteParcel(parcel: ParcelEntity)
    
    @Query("DELETE FROM parcels WHERE id = :id")
    suspend fun deleteParcelById(id: String)
}

