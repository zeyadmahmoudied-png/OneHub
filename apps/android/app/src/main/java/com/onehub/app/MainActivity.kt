package com.onehub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onehub.app.ui.AppDetailScreen
import com.onehub.app.ui.AppsScreen
import com.onehub.app.ui.HomeScreen
import com.onehub.app.ui.LoginScreen
import com.onehub.app.ui.RegisterScreen
import com.onehub.app.ui.theme.OneHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneHubTheme {
                val nav = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
                    NavHost(
                        navController = nav,
                        startDestination = "home",
                        modifier = Modifier.padding(inner),
                    ) {
                        composable("home") {
                            HomeScreen(
                                onApps = { nav.navigate("apps") },
                                onLogin = { nav.navigate("login") },
                                onRegister = { nav.navigate("register") },
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onSuccess = {
                                    nav.navigate("apps") {
                                        popUpTo("home")
                                    }
                                },
                                onRegister = { nav.navigate("register") },
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onSuccess = {
                                    nav.navigate("apps") {
                                        popUpTo("home")
                                    }
                                },
                                onLogin = { nav.navigate("login") },
                            )
                        }
                        composable("apps") {
                            AppsScreen(onAppClick = { id -> nav.navigate("app/$id") })
                        }
                        composable("app/{appId}") { entry ->
                            val id = entry.arguments?.getString("appId") ?: return@composable
                            AppDetailScreen(appId = id, onBack = { nav.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
