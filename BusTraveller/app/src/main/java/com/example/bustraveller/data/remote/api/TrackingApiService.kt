package com.example.bustraveller.data.remote.api

import com.example.bustraveller.data.remote.dto.LocationUpdateDto
import com.example.bustraveller.data.remote.dto.LoginRequest
import com.example.bustraveller.data.remote.dto.LoginResponse
import com.example.bustraveller.data.remote.dto.ParcelDto
import com.example.bustraveller.data.remote.dto.VehicleDto
import retrofit2.Response
import retrofit2.http.*

interface TrackingApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
    
    @GET("auth/verify")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<Map<String, Any>>
    
    // Vehicles
    @GET("vehicles")
    suspend fun getAllVehicles(): Response<List<VehicleDto>>
    
    @GET("vehicles/{id}")
    suspend fun getVehicleById(@Path("id") id: String): Response<VehicleDto>
    
    @POST("vehicles")
    suspend fun createVehicle(@Body vehicle: VehicleDto): Response<VehicleDto>
    
    @PUT("vehicles/{id}")
    suspend fun updateVehicle(@Path("id") id: String, @Body vehicle: VehicleDto): Response<Unit>
    
    @PATCH("vehicles/{id}/location")
    suspend fun updateVehicleLocation(
        @Path("id") id: String,
        @Body location: LocationUpdateDto
    ): Response<Unit>
    
    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): Response<Unit>
    
    // Parcels
    @GET("parcels")
    suspend fun getAllParcels(): Response<List<ParcelDto>>
    
    @GET("parcels/{id}")
    suspend fun getParcelById(@Path("id") id: String): Response<ParcelDto>
    
    @POST("parcels")
    suspend fun createParcel(@Body parcel: ParcelDto): Response<ParcelDto>
    
    @PUT("parcels/{id}")
    suspend fun updateParcel(@Path("id") id: String, @Body parcel: ParcelDto): Response<Unit>
    
    @PATCH("parcels/{id}/location")
    suspend fun updateParcelLocation(
        @Path("id") id: String,
        @Body location: LocationUpdateDto
    ): Response<Unit>
    
    @DELETE("parcels/{id}")
    suspend fun deleteParcel(@Path("id") id: String): Response<Unit>
}

