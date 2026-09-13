package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class GpsStatus(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKmToMhalma: Double = 0.0,
    val isInsideMhalma: Boolean = true,
    val isSimulatedDemo: Boolean = true,
    val permissionGranted: Boolean = false,
    val isChecking: Boolean = false,
    val statusMessage: String = "Position Mhalma vérifiée"
)

class LocationManagerHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _gpsStatus = MutableStateFlow(
        GpsStatus(
            latitude = MHALMA_LAT,
            longitude = MHALMA_LNG,
            distanceKmToMhalma = 0.3,
            isInsideMhalma = true,
            isSimulatedDemo = true,
            statusMessage = "Localisé à Mhalma Centre (Mode résident)"
        )
    )
    val gpsStatus: StateFlow<GpsStatus> = _gpsStatus.asStateFlow()

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun setSimulatedDemoMode(enabled: Boolean, allowedRadiusKm: Double) {
        if (enabled) {
            _gpsStatus.value = GpsStatus(
                latitude = MHALMA_LAT + 0.002,
                longitude = MHALMA_LNG + 0.002,
                distanceKmToMhalma = 0.3,
                isInsideMhalma = true,
                isSimulatedDemo = true,
                permissionGranted = hasLocationPermission(),
                isChecking = false,
                statusMessage = "Localisé à Mhalma Centre (~0.3 km, Rayon: ${allowedRadiusKm.roundToInt()} km)"
            )
        } else {
            // Re-check actual GPS
            checkActualGps(allowedRadiusKm)
        }
    }

    @SuppressLint("MissingPermission")
    fun checkActualGps(allowedRadiusKm: Double) {
        val hasPerm = hasLocationPermission()
        if (!hasPerm) {
            _gpsStatus.value = _gpsStatus.value.copy(
                permissionGranted = false,
                isChecking = false,
                statusMessage = "Autorisation GPS requise pour vérifier la résidence à Mhalma"
            )
            return
        }

        _gpsStatus.value = _gpsStatus.value.copy(isChecking = true, permissionGranted = true)

        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    processLocation(loc.latitude, loc.longitude, allowedRadiusKm, isDemo = false)
                } else {
                    // Try last location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            processLocation(lastLoc.latitude, lastLoc.longitude, allowedRadiusKm, isDemo = false)
                        } else {
                            // Fallback to simulated Mhalma position with notification
                            _gpsStatus.value = GpsStatus(
                                latitude = MHALMA_LAT,
                                longitude = MHALMA_LNG,
                                distanceKmToMhalma = 0.1,
                                isInsideMhalma = true,
                                isSimulatedDemo = true,
                                permissionGranted = true,
                                isChecking = false,
                                statusMessage = "Signal GPS indisponible, localisation par défaut Mhalma activée"
                            )
                        }
                    }
                }
            }
            .addOnFailureListener {
                _gpsStatus.value = _gpsStatus.value.copy(
                    isChecking = false,
                    statusMessage = "Erreur lecture GPS, vérifiez que le GPS est activé"
                )
            }
    }

    fun processLocation(lat: Double, lng: Double, allowedRadiusKm: Double, isDemo: Boolean) {
        val distanceMeters = FloatArray(1)
        Location.distanceBetween(lat, lng, MHALMA_LAT, MHALMA_LNG, distanceMeters)
        val distanceKm = (distanceMeters[0] / 1000.0)
        val formattedDist = (distanceKm * 10).roundToInt() / 10.0
        val isInside = distanceKm <= allowedRadiusKm

        val msg = if (isInside) {
            "Habitant validé : à $formattedDist km du centre de Mhalma (Rayon max: ${allowedRadiusKm.roundToInt()} km)"
        } else {
            "Hors zone : à $formattedDist km de Mhalma (Rayon max autorisé: ${allowedRadiusKm.roundToInt()} km)"
        }

        _gpsStatus.value = GpsStatus(
            latitude = lat,
            longitude = lng,
            distanceKmToMhalma = formattedDist,
            isInsideMhalma = isInside,
            isSimulatedDemo = isDemo,
            permissionGranted = hasLocationPermission(),
            isChecking = false,
            statusMessage = msg
        )
    }

    companion object {
        // Coordonnées géographiques du centre de Mhalma (Wilaya d'Alger, Daïra de Zéralda)
        const val MHALMA_LAT = 36.6775
        const val MHALMA_LNG = 2.8745
    }
}
