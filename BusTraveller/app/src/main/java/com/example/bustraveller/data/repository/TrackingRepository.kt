package com.example.bustraveller.data.repository

import android.content.Context
import com.example.bustraveller.data.local.AuthManager
import com.example.bustraveller.data.local.DatabaseProvider
import com.example.bustraveller.data.local.mapper.EntityMapper.toDomainModel
import com.example.bustraveller.data.local.mapper.EntityMapper.toEntity
import com.example.bustraveller.data.model.TrackableItem
import com.example.bustraveller.data.remote.RetrofitClient
import com.example.bustraveller.data.remote.dto.LoginRequest
import com.example.bustraveller.data.remote.dto.LocationUpdateDto
import com.example.bustraveller.data.remote.mapper.ApiMapper.toDto
import com.example.bustraveller.data.remote.mapper.ApiMapper.toEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import retrofit2.HttpException
import java.io.IOException

class TrackingRepository(context: Context, private val authManager: AuthManager) {
    private val database = DatabaseProvider.getDatabase(context)
    private val vehicleDao = database.vehicleDao()
    private val parcelDao = database.parcelDao()
    private val apiService = RetrofitClient.apiService
    
    fun isAdminLoggedIn(): Boolean {
        return authManager.isLoggedIn()
    }
    
    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                authManager.saveToken(loginResponse.token, loginResponse.username)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        try {
            val token = authManager.getAuthHeader()
            if (token != null) {
                apiService.logout(token)
            }
        } catch (e: Exception) {
            // Ignore errors on logout
        } finally {
            authManager.logout()
        }
    }
    
    val trackedItems: Flow<List<TrackableItem>> = combine(
        vehicleDao.getAllVehicles(),
        parcelDao.getAllParcels()
    ) { vehicles, parcels ->
        vehicles.map { it.toDomainModel() } + parcels.map { it.toDomainModel() }
    }
    
    suspend fun updateLocation(itemId: String, newLocation: LatLng) {
        val vehicle = vehicleDao.getVehicleById(itemId)
        if (vehicle != null) {
            val speed = Random.nextFloat() * 60f
            val heading = Random.nextFloat() * 360f
            vehicleDao.updateLocation(
                id = itemId,
                latitude = newLocation.latitude,
                longitude = newLocation.longitude,
                updateTime = System.currentTimeMillis(),
                speed = speed,
                heading = heading
            )
            // Sync with backend
            syncVehicleLocationToBackend(itemId, newLocation, speed, heading)
        } else {
            val parcel = parcelDao.getParcelById(itemId)
            if (parcel != null) {
                parcelDao.updateLocation(
                    id = itemId,
                    latitude = newLocation.latitude,
                    longitude = newLocation.longitude,
                    updateTime = System.currentTimeMillis()
                )
                // Sync with backend
                syncParcelLocationToBackend(itemId, newLocation)
            }
        }
    }
    
    private suspend fun syncVehicleLocationToBackend(
        itemId: String,
        location: LatLng,
        speed: Float,
        heading: Float
    ) {
        try {
            apiService.updateVehicleLocation(
                itemId,
                LocationUpdateDto(location.latitude, location.longitude, speed, heading)
            )
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun syncParcelLocationToBackend(itemId: String, location: LatLng) {
        try {
            apiService.updateParcelLocation(
                itemId,
                LocationUpdateDto(location.latitude, location.longitude)
            )
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
    }
    
    suspend fun getItemById(id: String): TrackableItem? {
        val vehicle = vehicleDao.getVehicleById(id)
        if (vehicle != null) return vehicle.toDomainModel()
        
        val parcel = parcelDao.getParcelById(id)
        return parcel?.toDomainModel()
    }
    
    suspend fun registerVehicle(
        name: String,
        routeNumber: String,
        driverName: String?,
        initialLocation: LatLng,
        departureLocation: String?,
        arrivalLocation: String?
    ): TrackableItem.Vehicle {
        val vehicle = TrackableItem.Vehicle(
            id = "vehicle_${System.currentTimeMillis()}",
            name = name,
            currentLocation = initialLocation,
            lastUpdateTime = System.currentTimeMillis(),
            status = com.example.bustraveller.data.model.VehicleStatus.DEPARTED,
            routeNumber = routeNumber,
            driverName = driverName,
            speed = 0f,
            heading = 0f,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation
        )
        val entity = vehicle.toEntity()
        vehicleDao.insertVehicle(entity)
        
        // Sync with backend
        try {
            apiService.createVehicle(entity.toDto())
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
        
        return vehicle
    }
    
    suspend fun registerParcel(
        name: String,
        trackingNumber: String,
        carrierName: String?,
        initialLocation: LatLng,
        estimatedDelivery: Long?
    ): TrackableItem.Parcel {
        val parcel = TrackableItem.Parcel(
            id = "parcel_${System.currentTimeMillis()}",
            name = name,
            currentLocation = initialLocation,
            lastUpdateTime = System.currentTimeMillis(),
            status = com.example.bustraveller.data.model.ParcelStatus.IN_TRANSIT,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            carrierName = carrierName
        )
        val entity = parcel.toEntity()
        parcelDao.insertParcel(entity)
        
        // Sync with backend
        try {
            apiService.createParcel(entity.toDto())
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
        
        return parcel
    }
    
    suspend fun updateVehicleStatus(itemId: String, newStatus: com.example.bustraveller.data.model.VehicleStatus) {
        vehicleDao.updateStatus(itemId, newStatus)
        
        // Sync with backend
        try {
            val vehicle = vehicleDao.getVehicleById(itemId)
            vehicle?.let {
                apiService.updateVehicle(itemId, it.toDto())
            }
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
    }
    
    suspend fun updateParcelStatus(itemId: String, newStatus: com.example.bustraveller.data.model.ParcelStatus) {
        parcelDao.updateStatus(itemId, newStatus)
        
        // Sync with backend
        try {
            val parcel = parcelDao.getParcelById(itemId)
            parcel?.let {
                apiService.updateParcel(itemId, it.toDto())
            }
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
    }
    
    suspend fun deleteItem(itemId: String) {
        // Try to delete from backend first
        try {
            apiService.deleteVehicle(itemId)
        } catch (e: Exception) {
            try {
                apiService.deleteParcel(itemId)
            } catch (e2: Exception) {
                // Silently fail - offline mode
            }
        }
        
        // Delete from local database
        vehicleDao.deleteVehicleById(itemId)
        parcelDao.deleteParcelById(itemId)
    }
    
    // Sync methods to fetch from backend
    suspend fun syncFromBackend() {
        try {
            // Sync vehicles
            val vehiclesResponse = apiService.getAllVehicles()
            if (vehiclesResponse.isSuccessful) {
                vehiclesResponse.body()?.let { vehicles ->
                    val entities = vehicles.map { it.toEntity() }
                    vehicleDao.insertVehicles(entities)
                }
            }
            
            // Sync parcels
            val parcelsResponse = apiService.getAllParcels()
            if (parcelsResponse.isSuccessful) {
                parcelsResponse.body()?.let { parcels ->
                    val entities = parcels.map { it.toEntity() }
                    parcelDao.insertParcels(entities)
                }
            }
        } catch (e: Exception) {
            // Silently fail - offline mode
            if (e !is IOException && e !is HttpException) {
                e.printStackTrace()
            }
        }
    }
}

