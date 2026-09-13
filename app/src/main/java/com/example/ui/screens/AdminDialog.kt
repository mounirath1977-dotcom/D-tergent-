package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppSettingsEntity
import com.example.model.AdItem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.DangerLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import kotlin.math.roundToInt

@Composable
fun AdminDialog(
    isAdminLoggedIn: Boolean,
    settings: AppSettingsEntity,
    adsList: List<AdItem>,
    onDismiss: () -> Unit,
    onLogin: (user: String, pass: String) -> Boolean,
    onLogout: () -> Unit,
    onUpdateGpsRadius: (Double) -> Unit,
    onUpdateAdmob: (Boolean) -> Unit,
    onToggleDemoLocation: (Boolean) -> Unit,
    onPurgeExpired: () -> Unit,
    onDeleteAd: (AdItem) -> Unit
) {
    var adminUserInput by remember { mutableStateOf("dziri-diou") }
    var adminPassInput by remember { mutableStateOf("212154") }
    var loginError by remember { mutableStateOf<String?>(null) }

    var sliderRadius by remember(settings.gpsRadiusKm) {
        mutableFloatStateOf(settings.gpsRadiusKm.toFloat())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            if (!isAdminLoggedIn) {
                // Admin Login Form
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(AmberLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AmberDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Portail Administrateur",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = "Gestion exclusive Mhalma Annonces",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Accès restreint. Veuillez saisir vos identifiants administrateur.",
                            fontSize = 12.sp,
                            color = AmberDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = adminUserInput,
                        onValueChange = {
                            adminUserInput = it
                            loginError = null
                        },
                        label = { Text("Nom d'utilisateur Admin") },
                        placeholder = { Text("dziri-diou") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = AmberDark)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admin_username_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminPassInput,
                        onValueChange = {
                            adminPassInput = it
                            loginError = null
                        },
                        label = { Text("Mot de passe") },
                        placeholder = { Text("212154") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AmberDark)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (loginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginError ?: "",
                            color = DangerRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val success = onLogin(adminUserInput, adminPassInput)
                            if (!success) {
                                loginError = "Identifiants incorrects (user: dziri-diou / mot de passe: 212154)"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admin_login_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary)
                    ) {
                        Text("Connexion Administrateur", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                // Admin Dashboard
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(EmeraldContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Console Administrateur",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = "Connecté: dziri-diou",
                                    fontSize = 12.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = onLogout) {
                                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Déconnexion", tint = DangerRed)
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // GPS Radius Setting (Admin requirement)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationSearching,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Rayon GPS Autorisé",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Slate800
                                    )
                                }

                                Text(
                                    text = "${sliderRadius.roundToInt()} km",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = EmeraldPrimary
                                )
                            }

                            Text(
                                text = "Centre fixé à Mhalma Alger (36.6775° N, 2.8745° E). Seuls les utilisateurs dans ce rayon peuvent utiliser l'application.",
                                fontSize = 11.sp,
                                color = Slate500,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Slider(
                                value = sliderRadius,
                                onValueChange = { sliderRadius = it },
                                valueRange = 1f..50f,
                                steps = 48,
                                colors = SliderDefaults.colors(
                                    thumbColor = EmeraldPrimary,
                                    activeTrackColor = EmeraldPrimary
                                ),
                                modifier = Modifier.testTag("gps_radius_slider")
                            )

                            Button(
                                onClick = { onUpdateGpsRadius(sliderRadius.toDouble()) },
                                modifier = Modifier.fillMaxWidth().testTag("save_gps_radius_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Enregistrer le rayon (${sliderRadius.roundToInt()} km)")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ads & Expiration Management
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gestion des Annonces (${adsList.size}/20)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate800
                                )

                                Button(
                                    onClick = onPurgeExpired,
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoDelete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Purger > 7 jours", fontSize = 11.sp)
                                }
                            }

                            Text(
                                text = "Toutes les annonces sont automatiquement supprimées après 1 semaine (7 jours).",
                                fontSize = 11.sp,
                                color = Slate500,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // List of ads with 1-click delete
                            adsList.take(6).forEach { ad ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = ad.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${ad.formattedPrice()} • ${ad.remainingTimeFormatted()}",
                                                fontSize = 11.sp,
                                                color = Slate500
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteAd(ad) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer",
                                                tint = DangerRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AdMob Ads Configuration
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdsClick,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Publicités AdMob",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Slate800
                                    )
                                }
                                Text(
                                    text = "Bannières et encarts sponsorisés locaux",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }

                            Switch(
                                checked = settings.admobEnabled,
                                onCheckedChange = onUpdateAdmob,
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // GPS Demo Simulation Mode
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mode Démo / Test GPS Mhalma",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate800
                                )
                                Text(
                                    text = "Permet de tester l'application hors de Mhalma en simulant la position",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }

                            Switch(
                                checked = settings.isDemoMhalmaLocation,
                                onCheckedChange = onToggleDemoLocation,
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerLight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Déconnexion Admin", color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
