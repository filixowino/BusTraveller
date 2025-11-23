package com.example.bustraveller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bustraveller.ui.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: TrackingViewModel,
    onRegistrationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<RegistrationType?>(null) }
    var vehicleName by remember { mutableStateOf("") }
    var routeNumber by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var departureLocation by remember { mutableStateOf("") }
    var arrivalLocation by remember { mutableStateOf("") }
    var parcelName by remember { mutableStateOf("") }
    var trackingNumber by remember { mutableStateOf("") }
    var carrierName by remember { mutableStateOf("") }
    
    val userLocation by viewModel.userLocation.collectAsState()
    val defaultLocation = LatLng(37.7749, -122.4194)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = "Register New Item",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Choose what you want to track",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Type Selection Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RegistrationTypeCard(
                type = RegistrationType.VEHICLE,
                selected = selectedType == RegistrationType.VEHICLE,
                onClick = { selectedType = RegistrationType.VEHICLE },
                modifier = Modifier.weight(1f)
            )
            
            RegistrationTypeCard(
                type = RegistrationType.PARCEL,
                selected = selectedType == RegistrationType.PARCEL,
                onClick = { selectedType = RegistrationType.PARCEL },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Form Fields
        selectedType?.let { type ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (type) {
                        RegistrationType.VEHICLE -> {
                            OutlinedTextField(
                                value = vehicleName,
                                onValueChange = { vehicleName = it },
                                label = { Text("Vehicle Name") },
                                placeholder = { Text("e.g., Bus Route 42") },
                                leadingIcon = { Icon(Icons.Default.Place, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = routeNumber,
                                onValueChange = { routeNumber = it },
                                label = { Text("Route Number") },
                                placeholder = { Text("e.g., 42") },
                                leadingIcon = { Icon(Icons.Default.Info, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = driverName,
                                onValueChange = { driverName = it },
                                label = { Text("Driver Name (Optional)") },
                                placeholder = { Text("Driver name") },
                                leadingIcon = { Icon(Icons.Default.Info, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = departureLocation,
                                onValueChange = { departureLocation = it },
                                label = { Text("Departure Location") },
                                placeholder = { Text("e.g., Central Station") },
                                leadingIcon = { Icon(Icons.Default.Place, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = arrivalLocation,
                                onValueChange = { arrivalLocation = it },
                                label = { Text("Arrival Location") },
                                placeholder = { Text("e.g., Downtown Terminal") },
                                leadingIcon = { Icon(Icons.Default.Place, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        RegistrationType.PARCEL -> {
                            OutlinedTextField(
                                value = parcelName,
                                onValueChange = { parcelName = it },
                                label = { Text("Parcel Name") },
                                placeholder = { Text("e.g., Package #12345") },
                                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = trackingNumber,
                                onValueChange = { trackingNumber = it },
                                label = { Text("Tracking Number") },
                                placeholder = { Text("e.g., TRK123456789") },
                                leadingIcon = { Icon(Icons.Default.Info, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = carrierName,
                                onValueChange = { carrierName = it },
                                label = { Text("Carrier Name (Optional)") },
                                placeholder = { Text("e.g., FastShip") },
                                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            val location = userLocation ?: defaultLocation
                            when (type) {
                                RegistrationType.VEHICLE -> {
                                    if (vehicleName.isNotBlank() && routeNumber.isNotBlank()) {
                                        viewModel.registerVehicle(
                                            vehicleName,
                                            routeNumber,
                                            driverName.ifBlank { null },
                                            location,
                                            departureLocation.ifBlank { null },
                                            arrivalLocation.ifBlank { null }
                                        )
                                        onRegistrationComplete()
                                    }
                                }
                                RegistrationType.PARCEL -> {
                                    if (parcelName.isNotBlank() && trackingNumber.isNotBlank()) {
                                        viewModel.registerParcel(
                                            parcelName,
                                            trackingNumber,
                                            carrierName.ifBlank { null },
                                            location
                                        )
                                        onRegistrationComplete()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 8.dp
                        ),
                        enabled = when (type) {
                            RegistrationType.VEHICLE -> vehicleName.isNotBlank() && routeNumber.isNotBlank() && departureLocation.isNotBlank() && arrivalLocation.isNotBlank()
                            RegistrationType.PARCEL -> parcelName.isNotBlank() && trackingNumber.isNotBlank()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Register",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationTypeCard(
    type: RegistrationType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (type) {
        RegistrationType.VEHICLE -> listOf(
            Color(0xFF4CAF50),
            Color(0xFF66BB6A)
        )
        RegistrationType.PARCEL -> listOf(
            Color(0xFFFF9800),
            Color(0xFFFFB74D)
        )
    }
    
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 12.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (selected) Brush.verticalGradient(gradientColors) else Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = when (type) {
                        RegistrationType.VEHICLE -> Icons.Default.Place
                        RegistrationType.PARCEL -> Icons.Default.ShoppingCart
                    },
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

enum class RegistrationType {
    VEHICLE, PARCEL
}

