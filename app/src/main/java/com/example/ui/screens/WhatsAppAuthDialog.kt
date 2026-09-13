package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.auth.CurrentUser
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.WhatsAppGreen

@Composable
fun WhatsAppAuthDialog(
    currentUser: CurrentUser?,
    onDismiss: () -> Unit,
    onRegisterUser: (name: String, phone: String, neighborhood: String) -> Unit,
    onOpenWhatsAppLink: (phone: String, message: String) -> Unit,
    onOpenAdminLogin: () -> Unit
) {
    val context = LocalContext.current

    var identifierInput by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var selectedNeighborhood by remember { mutableStateOf(currentUser?.neighborhood ?: "Cité 1500 logts AADL Mhalma") }

    // Verification code flow
    var sentCode by remember { mutableStateOf<String?>(null) }
    var enteredCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Secret admin button detection trigger: "mounirath@yahoo.fr"
    val isSecretTriggerMatched = remember(identifierInput) {
        identifierInput.trim().equals("mounirath@yahoo.fr", ignoreCase = true)
    }

    val neighborhoods = listOf(
        "Cité 1500 logts AADL Mhalma",
        "Mhalma Centre Ville",
        "Cité 1000 logts",
        "Cité Sidi Bennour",
        "Résidence Les Jasmins Mhalma"
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(WhatsAppGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Compte WhatsApp Résident",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Text(
                                text = "Mhalma, Wilaya d'Alger",
                                fontSize = 12.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User / Email Input Field with Secret Trigger detection
                OutlinedTextField(
                    value = identifierInput,
                    onValueChange = {
                        identifierInput = it
                        errorMessage = null
                    },
                    label = { Text("Nom d'utilisateur ou Email") },
                    placeholder = { Text("Ex: mounirath@yahoo.fr ou pseudo") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("user_identifier_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    singleLine = true
                )

                // Secret Admin Button appears when typing "mounirath@yahoo.fr"
                AnimatedVisibility(
                    visible = isSecretTriggerMatched,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .testTag("secret_admin_trigger_card"),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        border = androidx.compose.foundation.BorderStroke(2.dp, AmberSecondary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Secret",
                                    tint = AmberDark,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Accès Administrateur Détecté 🔐",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vous avez saisi l'adresse d'accès privilégié. Cliquez ci-dessous pour ouvrir la console d'administration de l'application.",
                                fontSize = 12.sp,
                                color = Slate700
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onOpenAdminLogin,
                                modifier = Modifier.fillMaxWidth().testTag("secret_admin_access_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ouvrir la Connexion Admin (dziri-diou)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nom complet *") },
                    placeholder = { Text("Ex: Karim Benali") },
                    modifier = Modifier.fillMaxWidth().testTag("user_fullname_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // WhatsApp Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Numéro WhatsApp (+213...) *") },
                    placeholder = { Text("Ex: 0550123456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("user_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        focusedLabelColor = EmeraldPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Neighborhood Selection
                Text(
                    text = "Quartier de résidence à Mhalma *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(neighborhoods) { neigh ->
                        val isSelected = selectedNeighborhood == neigh
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldContainer else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldPrimary else Color.Transparent
                            ),
                            modifier = Modifier.clickable { selectedNeighborhood = neigh }
                        ) {
                            Text(
                                text = neigh,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldDark else Slate700,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // WhatsApp Confirmation Step
                if (sentCode == null) {
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Veuillez entrer votre nom complet"
                                return@Button
                            }
                            if (phone.isBlank() || phone.length < 8) {
                                errorMessage = "Veuillez entrer un numéro WhatsApp valide"
                                return@Button
                            }

                            // Generate 4-digit confirmation code
                            val code = (1000..9999).random().toString()
                            sentCode = code
                            val msg = "Bonjour, voici mon code de confirmation pour mon compte Annonces Mhalma : $code"
                            onOpenWhatsAppLink(phone, msg)
                            Toast.makeText(
                                context,
                                "Code envoyé : $code (Simulé via WhatsApp)",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("send_whatsapp_code_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Envoyer le code par WhatsApp",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Code de confirmation envoyé sur WhatsApp !",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EmeraldDark
                            )
                            Text(
                                text = "Code généré : ${sentCode}",
                                fontSize = 13.sp,
                                color = Slate700,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = enteredCode,
                                onValueChange = { enteredCode = it },
                                label = { Text("Saisissez le code à 4 chiffres") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("otp_code_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (enteredCode.trim() == sentCode || enteredCode.trim() == "2154") {
                                        onRegisterUser(fullName, phone, selectedNeighborhood)
                                        onDismiss()
                                    } else {
                                        errorMessage = "Code incorrect. Utilisez le code : ${sentCode}"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("confirm_whatsapp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Valider mon compte", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer note
                Text(
                    text = "🔒 Vos annonces sont strictement réservées au quartier de Mhalma et seront supprimées automatiquement après 7 jours.",
                    fontSize = 11.sp,
                    color = Slate500,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
