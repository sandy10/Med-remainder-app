package com.dosemate.android.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.dosemate.android.R
import com.dosemate.android.presentation.navigation.Screen

/**
 * Bottom navigation bar for the main app shell.
 * Always shows icon + label for cognitive clarity (per design system guidelines).
 * Using Type-Safe Navigation destinations.
 *
 * @param currentDestination The currently active nav destination — used to highlight the active tab.
 * @param onNavigate         Callback invoked with the target [Screen] when a tab is tapped.
 */
@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    // Define nav items: label resource, icon, destination screen class
    val items = listOf(
        Triple(R.string.nav_home,        Icons.Default.Home,      Screen.Home),
        Triple(R.string.nav_medications, Icons.Default.List,      Screen.MedicationList),
        Triple(R.string.nav_history,     Icons.Default.DateRange, Screen.History),
        Triple(R.string.nav_settings,    Icons.Default.Person,    Screen.Settings)
    )

    NavigationBar(
        containerColor = Color.White, // Premium clarity
        tonalElevation = 0.dp // No grey tonal surface
    ) {
        items.forEach { (labelRes, icon, screen) ->
            val isSelected = currentDestination?.hasRoute(screen::class) == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = labelRes),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}
