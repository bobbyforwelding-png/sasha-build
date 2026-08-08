// Updated Ktor client for Android engine to resolve NoClassDefFoundError
package com.example.di

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import javax.inject.Qualifier
import javax.inject.Singleton
import com.example.BuildConfig
import com.example.domain.repository.VaultRepository
import com.example.data.repository.VaultRepositoryImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiApiKey

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVaultRepository(@ApplicationContext context: Context): VaultRepository {
        return VaultRepositoryImpl(context)
    }

    @Provides
    @Singleton
    @GeminiApiKey
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            engine {
                connectTimeout = 15000
                socketTimeout = 15000
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }
}
