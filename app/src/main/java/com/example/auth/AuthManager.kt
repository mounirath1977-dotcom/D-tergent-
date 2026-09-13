package com.example.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder

data class CurrentUser(
    val userId: String,
    val fullName: String,
    val phone: String,
    val whatsappNumber: String,
    val neighborhood: String,
    val isVerified: Boolean
)

class AuthManager(private val context: Context) {
    // Current authenticated WhatsApp resident user
    private val _currentUser = MutableStateFlow<CurrentUser?>(
        CurrentUser(
            userId = "resident_mhalma_default",
            fullName = "Résident Mhalma",
            phone = "0550123456",
            whatsappNumber = "213550123456",
            neighborhood = "Cité AADL Mhalma",
            isVerified = true
        )
    )
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    // Admin login state
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Secret trigger constant
    companion object {
        const val SECRET_ADMIN_TRIGGER = "mounirath@yahoo.fr"
        const val ADMIN_USERNAME = "dziri-diou"
        const val ADMIN_PASSWORD = "212154"
    }

    fun isSecretTrigger(input: String): Boolean {
        return input.trim().equals(SECRET_ADMIN_TRIGGER, ignoreCase = true)
    }

    fun checkAdminCredentials(user: String, pass: String): Boolean {
        val success = user.trim() == ADMIN_USERNAME && pass.trim() == ADMIN_PASSWORD
        if (success) {
            _isAdminLoggedIn.value = true
        }
        return success
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun loginUser(user: CurrentUser) {
        _currentUser.value = user
    }

    fun logoutUser() {
        _currentUser.value = null
    }

    // Helper to launch WhatsApp message for verification or seller contact
    fun openWhatsApp(phoneNumber: String, message: String) {
        try {
            // Clean phone number (convert local 05/06/07 to international 2135/2136/2137)
            val cleanedNumber = cleanPhoneNumber(phoneNumber)
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://wa.me/$cleanedNumber?text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer if WhatsApp not installed
            openDialer(phoneNumber)
        }
    }

    fun openDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun cleanPhoneNumber(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")
        return when {
            digits.startsWith("0") -> "213" + digits.substring(1)
            digits.startsWith("213") -> digits
            else -> "213$digits"
        }
    }
}
