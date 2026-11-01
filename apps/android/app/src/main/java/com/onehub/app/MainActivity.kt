package com.onehub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.onehub.accesscontrol.repo.InMemoryAccessRepository
import com.onehub.app.ui.AppDetailScreen
import com.onehub.app.ui.AppsScreen
import com.onehub.app.ui.HomeScreen
import com.onehub.app.ui.LoginScreen
import com.onehub.app.ui.OwnerAdminScreen
import com.onehub.app.ui.OwnerLoginScreen
import com.onehub.app.ui.OwnerSetupScreen
import com.onehub.app.ui.RedeemCodeScreen
import com.onehub.app.ui.RegisterScreen
import com.onehub.core.design.OneHubTheme
import com.onehub.navigation.OneHubNavHost
import com.onehub.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneHubTheme {
                val nav = rememberNavController()
                val accessRepo = remember { InMemoryAccessRepository() }

                Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
                    OneHubNavHost(
                        navController = nav,
                        modifier = Modifier.padding(inner),
                        startDestination = Routes.HOME,
                        home = {
                            HomeScreen(
                                onApps = { nav.navigate(Routes.HUB_APPS) },
                                onFeatureSlots = { nav.navigate(Routes.FEATURE_SLOTS) },
                                onLogin = { nav.navigate(Routes.LOGIN) },
                                onRegister = { nav.navigate(Routes.REGISTER) },
                                onOwnerSetup = { nav.navigate(Routes.ACCESS_OWNER_SETUP) },
                                onOwnerLogin = { nav.navigate(Routes.ACCESS_OWNER_LOGIN) },
                                onRedeem = { nav.navigate(Routes.ACCESS_REDEEM) },
                                onAdmin = { nav.navigate(Routes.ACCESS_ADMIN) },
                            )
                        },
                        login = {
                            LoginScreen(
                                onSuccess = {
                                    nav.navigate(Routes.HUB_APPS) {
                                        popUpTo(Routes.HOME)
                                    }
                                },
                                onRegister = { nav.navigate(Routes.REGISTER) },
                            )
                        },
                        register = {
                            RegisterScreen(
                                onSuccess = {
                                    nav.navigate(Routes.HUB_APPS) {
                                        popUpTo(Routes.HOME)
                                    }
                                },
                                onLogin = { nav.navigate(Routes.LOGIN) },
                            )
                        },
                        hubApps = {
                            AppsScreen(onAppClick = { id -> nav.navigate(Routes.hubApp(id)) })
                        },
                        hubAppDetail = { id ->
                            AppDetailScreen(appId = id, onBack = { nav.popBackStack() })
                        },
                        accessOwnerSetup = {
                            OwnerSetupScreen(
                                repo = accessRepo,
                                onDone = { nav.navigate(Routes.ACCESS_ADMIN) },
                                onBack = { nav.popBackStack() },
                            )
                        },
                        accessOwnerLogin = {
                            OwnerLoginScreen(
                                repo = accessRepo,
                                onDone = { nav.navigate(Routes.ACCESS_ADMIN) },
                                onBack = { nav.popBackStack() },
                            )
                        },
                        accessAdmin = {
                            OwnerAdminScreen(
                                repo = accessRepo,
                                onBack = { nav.popBackStack() },
                            )
                        },
                        accessRedeem = {
                            RedeemCodeScreen(
                                repo = accessRepo,
                                onBack = { nav.popBackStack() },
                            )
                        },
                    )
                }
            }
        }
    }
}
