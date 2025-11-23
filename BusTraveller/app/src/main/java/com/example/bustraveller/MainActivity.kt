package com.example.bustraveller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bustraveller.data.repository.TrackingRepository
import com.example.bustraveller.location.LocationTracker
import com.example.bustraveller.ui.screens.MainScreen
import com.example.bustraveller.ui.theme.BusTravellerTheme
import com.example.bustraveller.ui.viewmodel.AuthViewModel
import com.example.bustraveller.ui.viewmodel.TrackingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val authManager = com.example.bustraveller.data.local.AuthManager(this)
        com.example.bustraveller.data.remote.RetrofitClient.initialize(authManager)
        
        val repository = TrackingRepository(this, authManager)
        val locationTracker = LocationTracker(this)
        
        setContent {
            BusTravellerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val trackingViewModel: TrackingViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return TrackingViewModel(repository, locationTracker) as T
                            }
                        }
                    )
                    
                    val authViewModel: AuthViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return AuthViewModel(repository) as T
                            }
                        }
                    )
                    
                    MainScreen(
                        viewModel = trackingViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}