package com.example.model

import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AdItem(
    val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUri: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerWhatsApp: String,
    val sellerUserId: String,
    val neighborhood: String = "Mhalma Centre",
    val latitude: Double = 36.6775,
    val longitude: Double = 2.8745,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Exactly 7 days (1 week) expiration
    val expiresAt: Long
        get() = createdAt + TimeUnit.DAYS.toMillis(7)

    fun isExpired(currentTime: Long = System.currentTimeMillis()): Boolean {
        return currentTime >= expiresAt
    }

    fun remainingTimeFormatted(currentTime: Long = System.currentTimeMillis()): String {
        val remainingMillis = expiresAt - currentTime
        if (remainingMillis <= 0) return "Expirée"

        val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24

        return when {
            days > 1 -> "Expire dans $days jours"
            days == 1L -> "Expire dans 1 jour et ${hours}h"
            hours > 0 -> "Expire dans ${hours}h"
            else -> "Expire très bientôt"
        }
    }

    fun formattedPrice(): String {
        if (price <= 0.0) return "Prix sur demande"
        val formatter = NumberFormat.getNumberInstance(Locale.FRENCH)
        return "${formatter.format(price.toLong())} DA"
    }
}

object AdCategories {
    val ALL = "Toutes"
    val IMMOBILIER = "Immobilier"
    val VEHICULES = "Véhicules"
    val ELECTRONIQUE = "Électronique"
    val MAISON = "Maison & Meubles"
    val SERVICES = "Services"
    val DIVERS = "Divers"

    val list = listOf(ALL, IMMOBILIER, VEHICULES, ELECTRONIQUE, MAISON, SERVICES, DIVERS)
}
