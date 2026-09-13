package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AdItem

@Entity(tableName = "ads")
data class AdEntity(
    @PrimaryKey(autoGenerate = true)
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
    val neighborhood: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long
) {
    fun toDomain(): AdItem = AdItem(
        id = id,
        title = title,
        description = description,
        price = price,
        category = category,
        imageUri = imageUri,
        sellerName = sellerName,
        sellerPhone = sellerPhone,
        sellerWhatsApp = sellerWhatsApp,
        sellerUserId = sellerUserId,
        neighborhood = neighborhood,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(ad: AdItem): AdEntity = AdEntity(
            id = ad.id,
            title = ad.title,
            description = ad.description,
            price = ad.price,
            category = ad.category,
            imageUri = ad.imageUri,
            sellerName = ad.sellerName,
            sellerPhone = ad.sellerPhone,
            sellerWhatsApp = ad.sellerWhatsApp,
            sellerUserId = ad.sellerUserId,
            neighborhood = ad.neighborhood,
            latitude = ad.latitude,
            longitude = ad.longitude,
            createdAt = ad.createdAt
        )
    }
}
