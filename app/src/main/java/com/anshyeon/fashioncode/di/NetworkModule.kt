package com.anshyeon.fashioncode.di

import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.network.ApiCallAdapterFactory
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FireBaseRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @FireBaseRetrofit
    @Singleton
    @Provides
    fun provideFireBaseRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.FIREBASE_REALTIME_DB_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideFireBaseClient(@FireBaseRetrofit retrofit: Retrofit): FireBaseApiClient {
        return retrofit.create(FireBaseApiClient::class.java)
    }
}