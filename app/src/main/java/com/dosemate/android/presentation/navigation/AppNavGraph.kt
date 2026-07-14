package com.dosemate.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dosemate.android.data.datastore.AppPreferences
import com.dosemate.android.presentation.screens.addmedication.AddMedicationScreen
import com.dosemate.android.presentation.screens.history.HistoryScreen
import com.dosemate.android.presentation.screens.home.HomeScreen
import com.dosemate.android.presentation.screens.medicationdetail.MedicationDetailScreen
import com.dosemate.android.presentation.screens.medicationlist.MedicationListScreen
import com.dosemate.android.presentation.screens.onboarding.OnboardingScreen
import com.dosemate.android.presentation.screens.settings.SettingsScreen
import com.dosemate.android.presentation.screens.splash.SplashScreen

/**
 * Main navigation graph for the application.
 * Handles routing between all MVP screens using Type-Safe Navigation.
 */
@Composable
fun AppNavGraph(
    appPreferences: AppPreferences
) {
    val navController = rememberNavController()
    // Read user profile to determine if onboarding is complete for splash routing
    val userProfile by appPreferences.userProfileFlow.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        // ── Auth / Launch ─────────────────────────────────────────
        
        composable<Screen.Splash> {
            SplashScreen(
                isOnboardingComplete = userProfile?.onboardingComplete == true,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Onboarding> {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        // ── Main Shell ────────────────────────────────────────────

        composable<Screen.Home> {
            HomeScreen(
                navController = navController,
                onNavigate = { screen ->
                    navController.navigate(screen) {
                        launchSingleTop = true
                        if (screen == Screen.Home) {
                            popUpTo(Screen.Home) { inclusive = false }
                        }
                    }
                }
            )
        }

        composable<Screen.MedicationList> {
            MedicationListScreen(
                navController = navController,
                onNavigate = { screen ->
                    navController.navigate(screen) { launchSingleTop = true }
                }
            )
        }

        composable<Screen.History> {
            HistoryScreen(
                navController = navController,
                onNavigate = { screen ->
                    navController.navigate(screen) { launchSingleTop = true }
                }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                navController = navController,
                onNavigate = { screen ->
                    navController.navigate(screen) {
                        launchSingleTop = true
                        if (screen == Screen.Home) {
                            popUpTo(Screen.Home) { inclusive = false }
                        }
                    }
                }
            )
        }

        // ── Detail Screens ────────────────────────────────────────

        composable<Screen.AddMedication> {
            AddMedicationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.MedicationDetail> { backStackEntry ->
            val detail: Screen.MedicationDetail = backStackEntry.toRoute()
            MedicationDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditMedication(detail.medicationId)) }
            )
        }

        composable<Screen.EditMedication> { backStackEntry ->
            com.dosemate.android.presentation.screens.editmedication.EditMedicationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
