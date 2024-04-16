package com.anshyeon.fashioncode.di

import android.content.Context
import androidx.room.Room
import com.anshyeon.fashioncode.data.local.AppDatabase
import com.anshyeon.fashioncode.data.local.dao.ClothesDao
import com.anshyeon.fashioncode.data.local.dao.StyleDao
import com.anshyeon.fashioncode.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providerClothesDao(appDatabase: AppDatabase): ClothesDao {
        return appDatabase.clothesDao()
    }

    @Provides
    fun providerStyleDao(appDatabase: AppDatabase): StyleDao {
        return appDatabase.stylesDao()
    }
}