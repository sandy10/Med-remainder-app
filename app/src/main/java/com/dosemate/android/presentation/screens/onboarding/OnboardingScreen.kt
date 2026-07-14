package com.dosemate.android.presentation.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dosemate.android.R
import com.dosemate.android.domain.model.UserRole
import com.dosemate.android.presentation.components.AppButton
import com.dosemate.android.presentation.components.AppSecondaryButton
import com.dosemate.android.presentation.components.AppTextField
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPager by remember { mutableStateOf(true) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showPager) {
                OnboardingPager(onFinishPager = { showPager = false })
            } else {
                when (state.currentStep) {
                    0 -> NameStep(
                        name = state.name,
                        onNameChange = viewModel::updateName,
                        onNext = viewModel::nextStep
                    )
                    1 -> RoleStep(
                        selectedRole = state.role,
                        onRoleSelect = {
                            viewModel.updateRole(it)
                            viewModel.nextStep()
                        },
                        onBack = viewModel::previousStep
                    )
                    2 -> PermissionStep(
                        isSaving = state.isSaving,
                        onComplete = {
                            viewModel.completeOnboarding(onNavigateToHome)
                        },
                        onBack = viewModel::previousStep
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.OnboardingPager(onFinishPager: () -> Unit) {
    val pages = listOf(
        OnboardingPageData(
            title = "Track Your Medications",
            description = "Never miss a dose again. Keep your health schedule organized and easy to follow.",
            icon = Icons.Default.DateRange
        ),
        OnboardingPageData(
            title = "Connect With Caregivers",
            description = "Share your progress securely with family members or your doctors.",
            icon = Icons.Default.Favorite
        ),
        OnboardingPageData(
            title = "Stay Healthy",
            description = "Build better habits and improve your well-being with daily tracking.",
            icon = Icons.Default.Star
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.weight(1f)
    ) { page ->
        val pageData = pages[page]
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pageData.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = pageData.title,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pageData.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))
    
    // Page Indicators
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pages.size) { iteration ->
            val color = if (pagerState.currentPage == iteration) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.outlineVariant
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))

    AppButton(
        text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
        onClick = {
            if (pagerState.currentPage < pages.size - 1) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            } else {
                onFinishPager()
            }
        }
    )
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
private fun ColumnScope.NameStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Text(
        text = stringResource(id = R.string.onboarding_welcome_title),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(id = R.string.onboarding_welcome_desc),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(32.dp))
    AppTextField(
        value = name,
        onValueChange = onNameChange,
        label = stringResource(id = R.string.onboarding_name_hint),
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Spacer(modifier = Modifier.weight(1f))
    AppButton(
        text = stringResource(id = R.string.btn_continue),
        onClick = onNext,
        enabled = name.isNotBlank()
    )
}

@Composable
private fun ColumnScope.RoleStep(selectedRole: UserRole, onRoleSelect: (UserRole) -> Unit, onBack: () -> Unit) {
    Text(
        text = stringResource(id = R.string.onboarding_role_title),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(id = R.string.onboarding_role_desc),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    AppSecondaryButton(
        text = stringResource(id = R.string.role_patient),
        onClick = { onRoleSelect(UserRole.PATIENT) }
    )
    Spacer(modifier = Modifier.height(16.dp))
    AppSecondaryButton(
        text = stringResource(id = R.string.role_caregiver),
        onClick = { onRoleSelect(UserRole.CAREGIVER) }
    )
    
    Spacer(modifier = Modifier.weight(1f))
    AppSecondaryButton(
        text = stringResource(id = R.string.back),
        onClick = onBack
    )
}

@Composable
private fun ColumnScope.PermissionStep(isSaving: Boolean, onComplete: () -> Unit, onBack: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Proceed anyway for MVP */ onComplete() }
    )

    Text(
        text = stringResource(id = R.string.onboarding_notif_title),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(id = R.string.onboarding_notif_desc),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.weight(1f))
    
    AppButton(
        text = stringResource(id = R.string.btn_allow_notif),
        isLoading = isSaving,
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onComplete()
            }
        }
    )
    Spacer(modifier = Modifier.height(16.dp))
    AppSecondaryButton(
        text = stringResource(id = R.string.back),
        onClick = onBack,
        enabled = !isSaving
    )
}
