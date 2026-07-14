package com.dosemate.android.di

import com.dosemate.android.data.repository.DoseLogRepositoryImpl
import com.dosemate.android.data.repository.MedicationRepositoryImpl
import com.dosemate.android.data.repository.PlaceholderRepositoryImpl
import com.dosemate.android.domain.repository.DoseLogRepository
import com.dosemate.android.domain.repository.MedicationRepository
import com.dosemate.android.domain.repository.PlaceholderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaceholderRepository(
        impl: PlaceholderRepositoryImpl
    ): PlaceholderRepository

    @Binds
    @Singleton
    abstract fun bindMedicationRepository(
        impl: MedicationRepositoryImpl
    ): MedicationRepository

    @Binds
    @Singleton
    abstract fun bindDoseLogRepository(
        impl: DoseLogRepositoryImpl
    ): DoseLogRepository
}
