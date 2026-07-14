package com.dosemate.android.presentation.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dosemate.android.R
import com.dosemate.android.presentation.theme.Primary
import com.dosemate.android.presentation.theme.Secondary
import kotlinx.coroutines.delay

/**
 * Splash screen shown on every app launch.
 * Animates in over 600ms, then waits 1.2s before routing.
 *
 * @param onNavigateToOnboarding Called if the user has NOT completed onboarding.
 * @param onNavigateToHome       Called if onboarding is already complete.
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    isOnboardingComplete: Boolean? = null
) {

    // Animate opacity from 0 → 1 for a smooth fade-in
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "splash_alpha"
    )

    // Use rememberUpdatedState to ensure the LaunchedEffect uses the latest value
    // of onboarding completion status after the delay finishes.
    val currentOnboardingComplete by androidx.compose.runtime.rememberUpdatedState(isOnboardingComplete)

    LaunchedEffect(key1 = true) {
        visible = true        // trigger fade-in
        delay(1800)           // wait for animation + brief hold
        
        // Wait until we actually have the onboarding status from DataStore
        // (currentOnboardingComplete will be non-null once the flow emits)
        androidx.compose.runtime.snapshotFlow { currentOnboardingComplete }
            .collect { status ->
                if (status != null) {
                    if (status) {
                        onNavigateToHome()
                    } else {
                        onNavigateToOnboarding()
                    }
                    // Break the collection once we've navigated
                    return@collect
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Abstract Background Shapes (Blurred)
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 40.dp)
                .blur(100.dp)
                .background(Primary.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 40.dp)
                .blur(100.dp)
                .background(Secondary.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Pill Shape
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 32.dp)
                        .rotate(45f)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, Secondary)
                            )
                        )
                )
                
                // Clock Symbol Overlay
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(id = R.string.cd_app_logo),
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = "MedDose",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Never miss your medicine again",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            // Loading Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Primary.copy(alpha = 0.1f),
                    strokeWidth = 4.dp
                )
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = Primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}
