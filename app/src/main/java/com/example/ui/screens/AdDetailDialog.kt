package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.AdItem
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.DangerLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.WhatsAppGreen

@Composable
fun AdDetailDialog(
    ad: AdItem,
    canDelete: Boolean,
    onDismiss: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Header with close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = ad.imageUri,
                        contentDescription = ad.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Top gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )

                    // Close icon
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }

                    // Category Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldPrimary)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = ad.category,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Expiration Countdown Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AmberSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ad.remainingTimeFormatted(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Content Section
                Column(modifier = Modifier.padding(18.dp)) {
                    // Price
                    Text(
                        text = ad.formattedPrice(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title
                    Text(
                        text = ad.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Neighborhood location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${ad.neighborhood} • Mhalma, Alger",
                            fontSize = 13.sp,
                            color = Slate700,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Description",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ad.description,
                                fontSize = 14.sp,
                                color = Slate700,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Seller Info Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ad.sellerName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate800
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Vérifié",
                                        tint = WhatsAppGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Habitant de Mhalma vérifié par WhatsApp",
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tél: ${ad.sellerPhone}",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // WhatsApp and Call Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onWhatsAppClick,
                            modifier = Modifier.weight(1.4f).testTag("whatsapp_contact_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                        ) {
                            Text(
                                text = "WhatsApp",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onCallClick,
                            modifier = Modifier.weight(1f).testTag("call_contact_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Appeler", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Delete button if owner or admin
                    if (canDelete) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.fillMaxWidth().testTag("delete_ad_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DangerLight,
                                contentColor = DangerRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supprimer cette annonce", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
