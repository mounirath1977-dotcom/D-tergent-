package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET gpsRadiusKm = :radiusKm WHERE id = 1")
    suspend fun updateGpsRadius(radiusKm: Double)

    @Query("UPDATE app_settings SET admobEnabled = :enabled WHERE id = 1")
    suspend fun updateAdmobEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET isDemoMhalmaLocation = :isDemo WHERE id = 1")
    suspend fun updateDemoLocation(isDemo: Boolean)
}
