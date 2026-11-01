package com.onehub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.onehub.features.slots.FeatureSlotDetailScreen
import com.onehub.features.slots.FeatureSlotsScreen

/**
 * Central internal navigation — no external app Intents.
 * Feature destinations keep a single back stack entry where possible.
 */
@Composable
fun OneHubNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.HOME,
    home: @Composable () -> Unit,
    login: @Composable () -> Unit,
    register: @Composable () -> Unit,
    hubApps: @Composable () -> Unit,
    hubAppDetail: @Composable (appId: String) -> Unit,
    accessOwnerSetup: @Composable () -> Unit,
    accessOwnerLogin: @Composable () -> Unit,
    accessAdmin: @Composable () -> Unit,
    accessRedeem: @Composable () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Routes.HOME) { home() }
        composable(Routes.LOGIN) { login() }
        composable(Routes.REGISTER) { register() }
        composable(Routes.HUB_APPS) { hubApps() }
        composable(
            route = Routes.HUB_APP_DETAIL,
            arguments = listOf(navArgument("appId") { type = NavType.StringType }),
        ) { entry ->
            hubAppDetail(entry.arguments?.getString("appId") ?: return@composable)
        }
        composable(Routes.FEATURE_SLOTS) {
            FeatureSlotsScreen(
                onOpen = { id ->
                    navController.navigate(Routes.feature(id)) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.FEATURE_DETAIL,
            arguments = listOf(navArgument("featureId") { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString("featureId") ?: return@composable
            FeatureSlotDetailScreen(
                featureId = id,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.ACCESS_OWNER_SETUP) { accessOwnerSetup() }
        composable(Routes.ACCESS_OWNER_LOGIN) { accessOwnerLogin() }
        composable(Routes.ACCESS_ADMIN) { accessAdmin() }
        composable(Routes.ACCESS_REDEEM) { accessRedeem() }
    }
}
