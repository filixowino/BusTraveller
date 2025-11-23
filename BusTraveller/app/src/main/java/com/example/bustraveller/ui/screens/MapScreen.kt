package com.example.bustraveller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bustraveller.data.model.TrackableItem
import com.example.bustraveller.ui.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: TrackingViewModel,
    modifier: Modifier = Modifier
) {
    val trackedItems by viewModel.trackedItems.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    
    val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
    val initialLocation = remember(userLocation) { userLocation ?: defaultLocation }
    val initialCameraPosition = remember(initialLocation) {
        CameraPosition.Builder()
            .target(initialLocation)
            .zoom(13f)
            .build()
    }
    val cameraPositionState = rememberCameraPositionState()
    
    // Set initial camera position
    LaunchedEffect(initialCameraPosition) {
        cameraPositionState.position = initialCameraPosition
    }
    
    // Update map when tracked items locations change (for live updates)
    LaunchedEffect(trackedItems) {
        // Map will automatically update markers when trackedItems change
        // This ensures live location updates are reflected on the map
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { viewModel.selectItem(null) },
            properties = MapProperties(
                isMyLocationEnabled = userLocation != null,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                compassEnabled = true
            )
        ) {
            // Draw user location
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }
            
            // Draw tracked items
            trackedItems.forEach { item ->
                val markerColor = when (item) {
                    is TrackableItem.Vehicle -> BitmapDescriptorFactory.HUE_GREEN
                    is TrackableItem.Parcel -> BitmapDescriptorFactory.HUE_ORANGE
                }
                
                val isSelected = selectedItem?.id == item.id
                
                Marker(
                    state = MarkerState(position = item.currentLocation),
                    title = item.name,
                    snippet = when (item) {
                        is TrackableItem.Vehicle -> "Route: ${item.routeNumber} | Speed: ${item.speed.toInt()} km/h"
                        is TrackableItem.Parcel -> "Tracking: ${item.trackingNumber}"
                    },
                    icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                    onClick = {
                        viewModel.selectItem(item)
                        true
                    }
                )
                
                // Highlight selected item
                if (isSelected) {
                    Circle(
                        center = item.currentLocation,
                        radius = 200.0,
                        fillColor = Color(0x3300FF00),
                        strokeColor = Color(0xFF00FF00),
                        strokeWidth = 3f
                    )
                }
            }
        }
        
        // Selected item info card
        selectedItem?.let { item ->
            SelectedItemCard(
                item = item,
                onDismiss = { viewModel.selectItem(null) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun SelectedItemCard(
    item: TrackableItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (item) {
                            is TrackableItem.Vehicle -> "Route ${item.routeNumber}"
                            is TrackableItem.Parcel -> item.trackingNumber
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    label = "Status",
                    value = when (item) {
                        is TrackableItem.Vehicle -> {
                            when (item.status) {
                                com.example.bustraveller.data.model.VehicleStatus.DEPARTED -> 
                                    if (item.departureLocation != null) "Departed from ${item.departureLocation}" else "Departed"
                                com.example.bustraveller.data.model.VehicleStatus.ARRIVED -> 
                                    if (item.arrivalLocation != null) "Arrived at ${item.arrivalLocation}" else "Arrived"
                            }
                        }
                        is TrackableItem.Parcel -> item.status.name.replace("_", " ")
                    }
                )
                
                when (item) {
                    is TrackableItem.Vehicle -> {
                        InfoChip(
                            icon = Icons.Default.LocationOn,
                            label = "Speed",
                            value = "${item.speed.toInt()} km/h"
                        )
                    }
                    is TrackableItem.Parcel -> {
                        item.estimatedDelivery?.let { deliveryTime ->
                            InfoChip(
                                icon = Icons.Default.Info,
                                label = "ETA",
                                value = formatTime(deliveryTime)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun formatTime(timestamp: Long): String {
    val minutes = ((timestamp - System.currentTimeMillis()) / 60000).toInt()
    return if (minutes > 0) "$minutes min" else "Arrived"
}

