package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.GpsStatus
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.DangerLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun GpsRestrictionScreen(
    gpsStatus: GpsStatus,
    allowedRadiusKm: Double,
    onRefreshGps: () -> Unit,
    onEnableDemoMode: () -> Unit,
    onOpenAdminPortal: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(DangerLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Zone Restreinte à Mhalma (Alger)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cette application est réservée exclusivement aux résidents de la commune de Mhalma et de ses environs.",
                fontSize = 14.sp,
                color = Slate500,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Distance détectée :", fontSize = 13.sp, color = Slate700)
                        Text(
                            text = "${gpsStatus.distanceKmToMhalma} km de Mhalma",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Rayon autorisé par l'admin :", fontSize = 13.sp, color = Slate700)
                        Text(
                            text = "${allowedRadiusKm.toInt()} km",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Centre géographique :", fontSize = 13.sp, color = Slate700)
                        Text(
                            text = "36.6775° N, 2.8745° E",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRefreshGps,
                modifier = Modifier.fillMaxWidth().testTag("refresh_gps_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualiser ma position GPS", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Demo mode toggle so that testers and reviewers can test all app features easily
            OutlinedButton(
                onClick = onEnableDemoMode,
                modifier = Modifier.fillMaxWidth().testTag("enable_demo_mode_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
            ) {
                Icon(imageVector = Icons.Default.TravelExplore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Activer Mode Démo (Résident Mhalma)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Portal Access Button
            OutlinedButton(
                onClick = onOpenAdminPortal,
                modifier = Modifier.fillMaxWidth().testTag("open_admin_portal_from_restriction_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberSecondary)
            ) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Accès Administrateur (Modifier le Rayon)")
            }
        }
    }
}
