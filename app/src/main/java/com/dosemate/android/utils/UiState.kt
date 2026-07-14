package com.dosemate.android.utils

/**
 * Generic sealed class for managing UI state across all screens.
 * Every screen composable must handle all 5 states.
 */
sealed class UiState<out T> {
    /** Default / no action taken yet. */
    object Idle : UiState<Nothing>()

    /** Data is being fetched or processed. Show LoadingView. */
    object Loading : UiState<Nothing>()

    /** Data loaded successfully. Show actual content. */
    data class Success<T>(val data: T) : UiState<T>()

    /** An error occurred. Show ErrorView with retry option. */
    data class Error(val message: String) : UiState<Nothing>()

    /** Operation succeeded but returned no data. Show EmptyStateView. */
    object Empty : UiState<Nothing>()
}
