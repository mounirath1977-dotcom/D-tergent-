package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val gpsRadiusKm: Double = 5.0, // Rayon GPS réglable par l'administrateur
    val mhalmaLatitude: Double = 36.6775,
    val mhalmaLongitude: Double = 2.8745,
    val admobEnabled: Boolean = true,
    val maxAdsLimit: Int = 20, // Limité à 20 annonces
    val isDemoMhalmaLocation: Boolean = true // Simulation pour tester facilement hors de Mhalma
)
