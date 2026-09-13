package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.auth.CurrentUser
import com.example.data.AppDatabase
import com.example.data.AppSettingsEntity
import com.example.data.AdRepository
import com.example.data.UserEntity
import com.example.location.GpsStatus
import com.example.location.LocationManagerHelper
import com.example.model.AdCategories
import com.example.model.AdItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AdRepository(
        adDao = database.adDao(),
        userDao = database.userDao(),
        settingsDao = database.settingsDao(),
        context = application
    )
    val authManager = AuthManager(application)
    val locationHelper = LocationManagerHelper(application)

    val currentUser: StateFlow<CurrentUser?> = authManager.currentUser
    val isAdminLoggedIn: StateFlow<Boolean> = authManager.isAdminLoggedIn
    val gpsStatus: StateFlow<GpsStatus> = locationHelper.gpsStatus

    val settings: StateFlow<AppSettingsEntity> = repository.settings
        .combine(MutableStateFlow(Unit)) { s, _ ->
            s ?: AppSettingsEntity()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsEntity()
        )

    val selectedCategory = MutableStateFlow(AdCategories.ALL)
    val searchQuery = MutableStateFlow("")

    private val rawAds = repository.activeAds

    val filteredAds: StateFlow<List<AdItem>> = combine(
        rawAds,
        selectedCategory,
        searchQuery
    ) { ads, category, query ->
        ads.filter { ad ->
            val matchesCat = category == AdCategories.ALL || ad.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    ad.title.contains(query, ignoreCase = true) ||
                    ad.description.contains(query, ignoreCase = true) ||
                    ad.neighborhood.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAdsAdmin: StateFlow<List<AdItem>> = repository.allAdsAdmin.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            // Check location initially with current radius
            locationHelper.setSimulatedDemoMode(
                enabled = settings.value.isDemoMhalmaLocation,
                allowedRadiusKm = settings.value.gpsRadiusKm
            )
        }
    }

    fun selectCategory(cat: String) {
        selectedCategory.value = cat
    }

    fun updateSearchQuery(q: String) {
        searchQuery.value = q
    }

    fun refreshGps() {
        locationHelper.checkActualGps(settings.value.gpsRadiusKm)
    }

    fun toggleDemoLocation(enableDemo: Boolean) {
        viewModelScope.launch {
            repository.updateDemoLocation(enableDemo)
            locationHelper.setSimulatedDemoMode(enableDemo, settings.value.gpsRadiusKm)
            _userMessage.emit(
                if (enableDemo) "Mode Démo Mhalma activé (Position locale simulée)"
                else "Lecture GPS en temps réel activée"
            )
        }
    }

    fun updateGpsRadius(radiusKm: Double) {
        viewModelScope.launch {
            repository.updateGpsRadius(radiusKm)
            // Re-eval GPS status with new radius
            if (gpsStatus.value.isSimulatedDemo) {
                locationHelper.setSimulatedDemoMode(true, radiusKm)
            } else {
                locationHelper.checkActualGps(radiusKm)
            }
            _userMessage.emit("Rayon GPS mis à jour : ${radiusKm.toInt()} km autour de Mhalma")
        }
    }

    fun updateAdmobEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAdmobEnabled(enabled)
            _userMessage.emit(if (enabled) "Publicités AdMob activées" else "Publicités AdMob masquées")
        }
    }

    fun publishAd(
        title: String,
        description: String,
        price: Double,
        category: String,
        imageUri: String,
        neighborhood: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user == null || !user.isVerified) {
                _userMessage.emit("Veuillez d'abord créer et valider votre compte WhatsApp résident.")
                return@launch
            }

            if (!gpsStatus.value.isInsideMhalma) {
                _userMessage.emit("Publication refusée : vous devez être situé à Mhalma selon la détection GPS.")
                return@launch
            }

            val newAd = AdItem(
                title = title.trim(),
                description = description.trim(),
                price = price,
                category = category,
                imageUri = imageUri,
                sellerName = user.fullName,
                sellerPhone = user.phone,
                sellerWhatsApp = user.whatsappNumber,
                sellerUserId = user.userId,
                neighborhood = neighborhood,
                createdAt = System.currentTimeMillis()
            )

            val result = repository.insertAd(newAd)
            result.onSuccess {
                _userMessage.emit("Annonce publiée avec succès pour 7 jours à Mhalma !")
            }.onFailure { error ->
                _userMessage.emit(error.message ?: "Erreur lors de la publication")
            }
        }
    }

    fun deleteAd(ad: AdItem) {
        viewModelScope.launch {
            val current = currentUser.value
            val isAdmin = isAdminLoggedIn.value

            val isOwner = current != null && (current.userId == ad.sellerUserId || current.phone == ad.sellerPhone)

            if (!isOwner && !isAdmin) {
                _userMessage.emit("Vous ne pouvez supprimer que vos propres annonces.")
                return@launch
            }

            val success = repository.deleteAd(ad.id)
            if (success) {
                _userMessage.emit("Annonce supprimée avec succès.")
            } else {
                _userMessage.emit("Impossible de supprimer l'annonce.")
            }
        }
    }

    fun purgeExpiredAds() {
        viewModelScope.launch {
            val purgedCount = repository.autoPurgeExpiredAds()
            _userMessage.emit(
                if (purgedCount > 0) "$purgedCount annonces expirées (> 7 jours) ont été supprimées."
                else "Toutes les annonces sont récentes et actives (aucune annonce > 7 jours)."
            )
        }
    }

    fun createWhatsAppAccount(
        fullName: String,
        phone: String,
        neighborhood: String
    ) {
        viewModelScope.launch {
            val cleanedPhone = authManager.cleanPhoneNumber(phone)
            val userId = "user_${cleanedPhone}"
            val user = CurrentUser(
                userId = userId,
                fullName = fullName.trim(),
                phone = phone.trim(),
                whatsappNumber = cleanedPhone,
                neighborhood = neighborhood.trim(),
                isVerified = true
            )

            repository.registerUser(
                UserEntity(
                    userId = userId,
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    whatsappNumber = cleanedPhone,
                    neighborhood = neighborhood.trim(),
                    isWhatsAppVerified = true
                )
            )

            authManager.loginUser(user)
            _userMessage.emit("Compte vérifié avec succès via WhatsApp ! Bienvenue $fullName.")
        }
    }

    fun loginAdmin(user: String, pass: String): Boolean {
        val ok = authManager.checkAdminCredentials(user, pass)
        if (ok) {
            viewModelScope.launch {
                _userMessage.emit("Connexion administrateur réussie. Bienvenue dziri-diou.")
            }
        }
        return ok
    }

    fun logoutAdmin() {
        authManager.logoutAdmin()
        viewModelScope.launch {
            _userMessage.emit("Déconnexion administrateur effectuée.")
        }
    }
}
