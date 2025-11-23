package com.example.bustraveller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bustraveller.ui.viewmodel.AuthViewModel
import com.example.bustraveller.ui.viewmodel.TrackingViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Menu)
    object Map : Screen("map", "Map", Icons.Default.Place)
    object List : Screen("list", "List", Icons.Default.List)
    object Register : Screen("register", "Register", Icons.Default.Add)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TrackingViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Bus & Parcel Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        if (isLoggedIn) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Admin",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        TextButton(onClick = {
                            authViewModel.logout()
                        }) {
                            Text(
                                "Logout",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val screens = listOf(Screen.Home, Screen.Map, Screen.List, Screen.Register)
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            val isTracking by viewModel.isTracking.collectAsState()
            FloatingActionButton(
                onClick = {
                    if (isTracking) {
                        viewModel.stopTracking()
                    } else {
                        viewModel.startTracking()
                    }
                },
                containerColor = if (isTracking) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isTracking) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                if (isTracking) {
                    // Use a Box to create a stop square icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onErrorContainer)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Tracking"
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route)
                    },
                    onNavigateToList = {
                        navController.navigate(Screen.List.route)
                    }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(viewModel = viewModel)
            }
            composable(Screen.List.route) {
                TrackingListScreen(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    onItemClick = { item ->
                        viewModel.selectItem(item)
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Map.route) { inclusive = true }
                        }
                    },
                    onDelete = { item ->
                        if (isLoggedIn) {
                            viewModel.deleteItem(item.id)
                        } else {
                            pendingAction = { viewModel.deleteItem(item.id) }
                            showLoginDialog = true
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                if (isLoggedIn) {
                    RegistrationScreen(
                        viewModel = viewModel,
                        onRegistrationComplete = {
                            navController.navigate(Screen.List.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    )
                } else {
                    AdminLoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            // Login successful - the screen will automatically update
                            // because isLoggedIn state changes and recomposes
                        }
                    )
                }
            }
        }
        
        // Login Dialog for delete operations
        if (showLoginDialog) {
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            val loginError by authViewModel.loginError.collectAsState()
            val isLoading by authViewModel.isLoading.collectAsState()
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    showLoginDialog = false
                    pendingAction?.invoke()
                    pendingAction = null
                }
            }
            
            AlertDialog(
                onDismissRequest = {
                    showLoginDialog = false
                    pendingAction = null
                },
                title = { Text("Admin Login Required") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("You need to login as admin to delete items.")
                        
                        OutlinedTextField(
                            value = username,
                            onValueChange = { 
                                username = it
                                authViewModel.clearError()
                            },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                authViewModel.clearError()
                            },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )
                        
                        if (loginError != null) {
                            Text(
                                text = loginError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            authViewModel.login(username, password)
                        },
                        enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showLoginDialog = false
                        pendingAction = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

