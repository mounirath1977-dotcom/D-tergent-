package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AdCategories
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun CreateAdDialog(
    currentCount: Int,
    maxLimit: Int = 20,
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, price: Double, category: String, imageUri: String, neighborhood: String) -> Unit
) {
    val context = LocalContext.current
    val pkg = context.packageName

    val presetImages = listOf(
        "android.resource://$pkg/${R.drawable.img_sample_apartment}" to "Immobilier",
        "android.resource://$pkg/${R.drawable.img_sample_car}" to "Véhicule",
        "android.resource://$pkg/${R.drawable.img_sample_tech}" to "Électronique"
    )

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AdCategories.IMMOBILIER) }
    var selectedNeighborhood by remember { mutableStateOf("Cité 1500 logts AADL Mhalma") }
    var selectedImageUri by remember { mutableStateOf(presetImages[0].first) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri.toString()
            }
        }
    )

    val neighborhoods = listOf(
        "Cité 1500 logts AADL Mhalma",
        "Mhalma Centre Ville",
        "Cité 1000 logts",
        "Cité Sidi Bennour",
        "Résidence Les Jasmins Mhalma",
        "Zone d'Activité Mhalma - Zéralda"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
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
                    Column {
                        Text(
                            text = "Nouvelle Annonce",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "Mhalma ($currentCount/$maxLimit annonces) • Valable 7 jours",
                            fontSize = 12.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Selection Section
                Text(
                    text = "Photo de l'annonce",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Selected Image Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE2E8F0))
                        .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Aperçu de la photo",
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentScale = ContentScale.Crop
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Choisir une photo", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Preset photos selection
                Text(text = "Ou sélectionner une photo type :", fontSize = 12.sp, color = Slate500)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presetImages) { (uri, label) ->
                        val isSelected = selectedImageUri == uri
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) EmeraldPrimary else Slate200,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedImageUri = uri }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = label,
                                modifier = Modifier.fillMaxWidth().height(64.dp),
                                contentScale = ContentScale.Crop
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .background(EmeraldPrimary, RoundedCornerShape(bottomStart = 6.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de l'annonce *") },
                    placeholder = { Text("Ex: Appartement F3 AADL, Golf 7...") },
                    modifier = Modifier.fillMaxWidth().testTag("ad_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selection
                Text(text = "Catégorie *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = AdCategories.list.filter { it != AdCategories.ALL }
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) EmeraldPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Slate700,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Input
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Prix en Dinars Algériens (DA) *") },
                    placeholder = { Text("Ex: 45000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("ad_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Neighborhood Selection
                Text(text = "Quartier / Cité à Mhalma *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(neighborhoods) { neigh ->
                        val isSelected = selectedNeighborhood == neigh
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldContainer else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldPrimary else Color.Transparent
                            ),
                            modifier = Modifier.clickable { selectedNeighborhood = neigh }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) EmeraldPrimary else Slate500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = neigh,
                                    fontSize = 12.sp,
                                    color = if (isSelected) OnEmeraldContainer else Slate700,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description complète *") },
                    placeholder = { Text("Détails, état, disponibilité, caractéristiques...") },
                    modifier = Modifier.fillMaxWidth().height(110.dp).testTag("ad_description_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    maxLines = 4
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                    ) {
                        Text("Annuler", color = Slate700)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Veuillez saisir un titre"
                                return@Button
                            }
                            val price = priceText.toDoubleOrNull()
                            if (price == null || price <= 0) {
                                errorMessage = "Veuillez entrer un prix valide"
                                return@Button
                            }
                            if (description.isBlank()) {
                                errorMessage = "Veuillez saisir une description"
                                return@Button
                            }

                            onPublish(
                                title,
                                description,
                                price,
                                selectedCategory,
                                selectedImageUri,
                                selectedNeighborhood
                            )
                        },
                        modifier = Modifier.weight(1.5f).testTag("publish_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Publier (7 jours)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
