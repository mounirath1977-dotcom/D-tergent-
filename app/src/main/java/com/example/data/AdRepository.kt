package com.example.data

import android.content.Context
import com.example.R
import com.example.model.AdCategories
import com.example.model.AdItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AdRepository(
    private val adDao: AdDao,
    private val userDao: UserDao,
    private val settingsDao: SettingsDao,
    private val context: Context
) {
    private val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)

    fun getCutoffTime(): Long = System.currentTimeMillis() - sevenDaysMillis

    val activeAds: Flow<List<AdItem>> = adDao.getActiveAds(getCutoffTime()).map { list ->
        list.map { it.toDomain() }
    }

    val allAdsAdmin: Flow<List<AdItem>> = adDao.getAllAdsAdmin().map { list ->
        list.map { it.toDomain() }
    }

    val settings: Flow<AppSettingsEntity?> = settingsDao.getSettingsFlow()

    suspend fun getActiveAdsCount(): Int {
        return adDao.getActiveAdsCount(getCutoffTime())
    }

    suspend fun autoPurgeExpiredAds(): Int {
        return adDao.deleteExpiredAds(getCutoffTime())
    }

    suspend fun insertAd(ad: AdItem): Result<Long> {
        // Auto-purge first
        autoPurgeExpiredAds()

        val currentCount = getActiveAdsCount()
        if (currentCount >= 20) {
            return Result.failure(Exception("La limite de 20 annonces pour Mhalma est atteinte. Supprimez une annonce ou attendez l'expiration d'une annonce."))
        }

        val id = adDao.insertAd(AdEntity.fromDomain(ad))
        return Result.success(id)
    }

    suspend fun deleteAd(adId: Long): Boolean {
        val rows = adDao.deleteAdById(adId)
        return rows > 0
    }

    suspend fun getUserAds(userId: String): Flow<List<AdItem>> {
        return adDao.getAdsByUser(userId, getCutoffTime()).map { list -> list.map { it.toDomain() } }
    }

    suspend fun updateGpsRadius(radiusKm: Double) {
        settingsDao.updateGpsRadius(radiusKm)
    }

    suspend fun updateAdmobEnabled(enabled: Boolean) {
        settingsDao.updateAdmobEnabled(enabled)
    }

    suspend fun updateDemoLocation(isDemo: Boolean) {
        settingsDao.updateDemoLocation(isDemo)
    }

    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUser(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun initializeDefaultsIfNeeded() {
        // Ensure settings exist
        val currentSettings = settingsDao.getSettingsSync()
        if (currentSettings == null) {
            settingsDao.saveSettings(
                AppSettingsEntity(
                    id = 1,
                    gpsRadiusKm = 5.0,
                    mhalmaLatitude = 36.6775,
                    mhalmaLongitude = 2.8745,
                    admobEnabled = true,
                    maxAdsLimit = 20,
                    isDemoMhalmaLocation = true
                )
            )
        }

        // Auto purge expired
        autoPurgeExpiredAds()

        // Seed initial ads if empty
        val count = getActiveAdsCount()
        if (count == 0) {
            seedSampleAds()
        }
    }

    private suspend fun seedSampleAds() {
        val now = System.currentTimeMillis()
        val pkg = context.packageName

        val apartmentUri = "android.resource://$pkg/${R.drawable.img_sample_apartment}"
        val carUri = "android.resource://$pkg/${R.drawable.img_sample_car}"
        val techUri = "android.resource://$pkg/${R.drawable.img_sample_tech}"

        val sampleAds = listOf(
            AdEntity(
                title = "Appartement F3 Cité AADL Mhalma",
                description = "Loue très bel appartement F3 refait à neuf, 4ème étage avec ascenseur, cuisine équipée, chauffe-bain, vue dégagée. Quartier calme à proximité école et commerces.",
                price = 38000.0,
                category = AdCategories.IMMOBILIER,
                imageUri = apartmentUri,
                sellerName = "Ahmed M.",
                sellerPhone = "0550123456",
                sellerWhatsApp = "213550123456",
                sellerUserId = "user_ahmed",
                neighborhood = "Cité 1500 logts AADL",
                latitude = 36.6780,
                longitude = 2.8750,
                createdAt = now - TimeUnit.HOURS.toMillis(4) // 4 hours ago
            ),
            AdEntity(
                title = "Seat Ibiza 1.6 TDI FR propre",
                description = "Seat Ibiza FR modèle 2019, 115 000 km réels, carnet d'entretien à jour, zéro retouche de peinture, 4 pneus neufs. Disponible pour visite à Mhalma centre.",
                price = 2650000.0,
                category = AdCategories.VEHICULES,
                imageUri = carUri,
                sellerName = "Sofiane B.",
                sellerPhone = "0661987654",
                sellerWhatsApp = "213661987654",
                sellerUserId = "user_sofiane",
                neighborhood = "Mhalma Centre",
                latitude = 36.6765,
                longitude = 2.8730,
                createdAt = now - TimeUnit.DAYS.toMillis(1) // 1 day ago
            ),
            AdEntity(
                title = "Smartphone & Casque Bluetooth Neuf",
                description = "Pack smartphone 128 Go débloqué tout opérateur + casque audio haute fidélité sans fil avec réduction de bruit, boîte et accessoires d'origine scellés.",
                price = 42000.0,
                category = AdCategories.ELECTRONIQUE,
                imageUri = techUri,
                sellerName = "Yacine Tech",
                sellerPhone = "0770554433",
                sellerWhatsApp = "213770554433",
                sellerUserId = "user_yacine",
                neighborhood = "Cité Sidi Bennour",
                latitude = 36.6790,
                longitude = 2.8760,
                createdAt = now - TimeUnit.DAYS.toMillis(2) // 2 days ago
            ),
            AdEntity(
                title = "Table à manger bois massif + 6 chaises",
                description = "Superbe table artisanale en hêtre massif avec 6 chaises rembourrées en velours beige, excellent état comme neuve. À récupérer à Mhalma.",
                price = 55000.0,
                category = AdCategories.MAISON,
                imageUri = apartmentUri,
                sellerName = "Nadia K.",
                sellerPhone = "0560778899",
                sellerWhatsApp = "213560778899",
                sellerUserId = "user_nadia",
                neighborhood = "Cité 1000 logts",
                latitude = 36.6770,
                longitude = 2.8720,
                createdAt = now - TimeUnit.DAYS.toMillis(3) // 3 days ago
            ),
            AdEntity(
                title = "Plombier Chauffagiste - Dépannage Mhalma",
                description = "Artisan qualifié disponible 7j/7 pour installations sanitaires, réparation fuites d'eau, chaudières, radiateurs et chauffe-eau sur tout Mhalma et Zéralda.",
                price = 2500.0,
                category = AdCategories.SERVICES,
                imageUri = techUri,
                sellerName = "Mourad Services",
                sellerPhone = "0540112233",
                sellerWhatsApp = "213540112233",
                sellerUserId = "user_mourad",
                neighborhood = "Zone Mhalma - Zéralda",
                latitude = 36.6775,
                longitude = 2.8745,
                createdAt = now - TimeUnit.HOURS.toMillis(18) // 18 hours ago
            )
        )

        sampleAds.forEach { ad ->
            adDao.insertAd(ad)
        }
    }
}
