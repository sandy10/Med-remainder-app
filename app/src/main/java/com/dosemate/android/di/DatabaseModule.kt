package com.dosemate.android.di

import android.content.Context
import androidx.room.Room
import com.dosemate.android.data.local.AppDatabase
import com.dosemate.android.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
        // TODO: Replace with proper migrations before production release.
        // fallbackToDestructiveMigration is used here for development speed.
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun providePlaceholderDao(db: AppDatabase) = db.placeholderDao

    @Provides
    @Singleton
    fun provideMedicationDao(db: AppDatabase) = db.medicationDao

    @Provides
    @Singleton
    fun provideDoseLogDao(db: AppDatabase) = db.doseLogDao
}
