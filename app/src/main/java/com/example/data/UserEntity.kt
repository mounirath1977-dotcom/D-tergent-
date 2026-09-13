package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String, // phone or identifier
    val fullName: String,
    val phone: String,
    val whatsappNumber: String,
    val neighborhood: String,
    val isWhatsAppVerified: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
