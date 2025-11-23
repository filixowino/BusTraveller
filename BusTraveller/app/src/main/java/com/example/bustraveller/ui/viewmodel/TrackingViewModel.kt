package com.example.bustraveller.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bustraveller.data.model.TrackableItem
import com.example.bustraveller.data.repository.TrackingRepository
import com.example.bustraveller.location.LocationTracker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackingViewModel(
    private val repository: TrackingRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {
    
    private val _trackedItems = MutableStateFlow<List<TrackableItem>>(emptyList())
    val trackedItems: StateFlow<List<TrackableItem>> = _trackedItems.asStateFlow()
    
    private val _selectedItem = MutableStateFlow<TrackableItem?>(null)
    val selectedItem: StateFlow<TrackableItem?> = _selectedItem.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filterType = MutableStateFlow<FilterType>(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()
    
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()
    
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.trackedItems.collect { items ->
                _trackedItems.value = items
            }
        }
        
        startLocationUpdates()
    }
    
    fun startLocationUpdates() {
        viewModelScope.launch {
            locationTracker.getLocationUpdates().collect { location ->
                location?.let { _userLocation.value = it }
            }
        }
    }
    
    fun selectItem(item: TrackableItem?) {
        _selectedItem.value = item
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setFilterType(type: FilterType) {
        _filterType.value = type
    }
    
    fun getFilteredItems(): StateFlow<List<TrackableItem>> {
        return combine(_trackedItems, _searchQuery, _filterType) { items, query, filter ->
            items.filter { item ->
                val matchesQuery = query.isEmpty() || 
                    item.name.contains(query, ignoreCase = true) ||
                    (item is TrackableItem.Vehicle && item.routeNumber.contains(query, ignoreCase = true)) ||
                    (item is TrackableItem.Parcel && item.trackingNumber.contains(query, ignoreCase = true))
                
                val matchesFilter = when (filter) {
                    FilterType.ALL -> true
                    FilterType.VEHICLES -> item is TrackableItem.Vehicle
                    FilterType.PARCELS -> item is TrackableItem.Parcel
                }
                
                matchesQuery && matchesFilter
            }
        }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())
    }
    
    fun simulateLocationUpdate(itemId: String) {
        viewModelScope.launch {
            val item = repository.getItemById(itemId)
            item?.let {
                val currentLocation = it.currentLocation
                val newLat = currentLocation.latitude + (kotlin.random.Random.nextDouble() - 0.5) * 0.01
                val newLng = currentLocation.longitude + (kotlin.random.Random.nextDouble() - 0.5) * 0.01
                repository.updateLocation(itemId, LatLng(newLat, newLng))
            }
        }
    }
    
    fun startTracking() {
        _isTracking.value = true
        // Start periodic updates for all items
        viewModelScope.launch {
            while (_isTracking.value) {
                _trackedItems.value.forEach { item ->
                    simulateLocationUpdate(item.id)
                }
                kotlinx.coroutines.delay(5000) // Update every 5 seconds
            }
        }
    }
    
    fun stopTracking() {
        _isTracking.value = false
    }
    
    fun registerVehicle(
        name: String,
        routeNumber: String,
        driverName: String?,
        initialLocation: LatLng,
        departureLocation: String?,
        arrivalLocation: String?
    ) {
        viewModelScope.launch {
            repository.registerVehicle(name, routeNumber, driverName, initialLocation, departureLocation, arrivalLocation)
        }
    }
    
    fun registerParcel(
        name: String,
        trackingNumber: String,
        carrierName: String?,
        initialLocation: LatLng
    ) {
        viewModelScope.launch {
            repository.registerParcel(name, trackingNumber, carrierName, initialLocation, null)
        }
    }
    
    fun updateVehicleStatus(itemId: String, status: com.example.bustraveller.data.model.VehicleStatus) {
        viewModelScope.launch {
            repository.updateVehicleStatus(itemId, status)
        }
    }
    
    fun updateParcelStatus(itemId: String, status: com.example.bustraveller.data.model.ParcelStatus) {
        viewModelScope.launch {
            repository.updateParcelStatus(itemId, status)
        }
    }
    
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }
}

enum class FilterType {
    ALL, VEHICLES, PARCELS
}

