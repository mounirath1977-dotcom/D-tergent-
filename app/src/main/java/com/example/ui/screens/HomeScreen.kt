package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AdCategories
import com.example.model.AdItem
import com.example.ui.components.AdCard
import com.example.ui.components.AdMobBanner
import com.example.ui.components.AdMobNativeCard
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.WhatsAppGreen
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val ads by viewModel.filteredAds.collectAsStateWithLifecycle()
    val allAdsAdmin by viewModel.allAdsAdmin.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val gpsStatus by viewModel.gpsStatus.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedAdDetail by remember { mutableStateOf<AdItem?>(null) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showAdminDialog by remember { mutableStateOf(false) }

    // Toast messages collector
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Geofencing enforcement: if user is outside Mhalma, show restriction screen
    if (!gpsStatus.isInsideMhalma && !settings.isDemoMhalmaLocation) {
        GpsRestrictionScreen(
            gpsStatus = gpsStatus,
            allowedRadiusKm = settings.gpsRadiusKm,
            onRefreshGps = { viewModel.refreshGps() },
            onEnableDemoMode = { viewModel.toggleDemoLocation(true) },
            onOpenAdminPortal = { showAdminDialog = true }
        )

        if (showAdminDialog) {
            AdminDialog(
                isAdminLoggedIn = isAdminLoggedIn,
                settings = settings,
                adsList = allAdsAdmin,
                onDismiss = { showAdminDialog = false },
                onLogin = { u, p -> viewModel.loginAdmin(u, p) },
                onLogout = { viewModel.logoutAdmin() },
                onUpdateGpsRadius = { r -> viewModel.updateGpsRadius(r) },
                onUpdateAdmob = { en -> viewModel.updateAdmobEnabled(en) },
                onToggleDemoLocation = { d -> viewModel.toggleDemoLocation(d) },
                onPurgeExpired = { viewModel.purgeExpiredAds() },
                onDeleteAd = { ad -> viewModel.deleteAd(ad) }
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Annonces Mhalma",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldLight)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Alger",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                            }
                            Text(
                                text = "Petites annonces régionales • 7 jours max",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                },
                actions = {
                    // GPS status chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (gpsStatus.isInsideMhalma) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (gpsStatus.isInsideMhalma) WhatsAppGreen else Color.Red
                        ),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable { viewModel.refreshGps() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (gpsStatus.isInsideMhalma) EmeraldPrimary else Color.Red,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (gpsStatus.isInsideMhalma) "Mhalma OK" else "Hors zone",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (gpsStatus.isInsideMhalma) EmeraldDark else Color.Red
                            )
                        }
                    }

                    // User WhatsApp profile / login button
                    IconButton(
                        onClick = { showAuthDialog = true },
                        modifier = Modifier.testTag("user_profile_button")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Compte WhatsApp",
                                tint = EmeraldPrimary
                            )
                            if (currentUser != null && currentUser?.isVerified == true) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(WhatsAppGreen, CircleShape)
                                        .border(1.dp, Color.White, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }

                    // Admin icon if logged in
                    if (isAdminLoggedIn) {
                        IconButton(
                            onClick = { showAdminDialog = true },
                            modifier = Modifier.testTag("admin_portal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin dziri-diou",
                                tint = AmberSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (ads.size >= settings.maxAdsLimit) {
                        Toast.makeText(
                            context,
                            "Limite de 20 annonces atteinte pour Mhalma. Supprimez une annonce existante.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (currentUser == null) {
                        Toast.makeText(
                            context,
                            "Veuillez vous inscrire avec votre compte WhatsApp pour publier.",
                            Toast.LENGTH_LONG
                        ).show()
                        showAuthDialog = true
                    } else {
                        showCreateDialog = true
                    }
                },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("create_ad_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Publier une annonce", modifier = Modifier.size(28.dp))
            }
        },
        bottomBar = {
            if (settings.admobEnabled) {
                AdMobBanner()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAF9))
        ) {
            // Quota Bar: Max 20 Annonces & Auto-Expiry Notice
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quota quartier : ${ads.size} / ${settings.maxAdsLimit} annonces actives",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ads.size >= 18) AmberDark else Slate800
                        )

                        Text(
                            text = "Auto-suppression 7 jours",
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { (ads.size.toFloat() / settings.maxAdsLimit.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (ads.size >= 19) Color(0xFFDC2626) else EmeraldPrimary,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            // Search Bar
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Rechercher à Mhalma (ex: F3, Golf, PS5...)", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f).testTag("search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Effacer", tint = Slate500, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Categories Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AdCategories.list) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) EmeraldPrimary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EmeraldPrimary else Slate200
                        ),
                        modifier = Modifier.clickable { viewModel.selectCategory(category) }
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Slate700,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Ads List
            if (ads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Aucune annonce trouvée à Mhalma",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Soyez le premier habitant à publier une petite annonce !",
                            fontSize = 13.sp,
                            color = Slate500
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(ads) { index, ad ->
                        val canDelete = isAdminLoggedIn || (currentUser != null && (currentUser?.userId == ad.sellerUserId || currentUser?.phone == ad.sellerPhone))

                        AdCard(
                            ad = ad,
                            canDelete = canDelete,
                            onClick = { selectedAdDetail = ad },
                            onWhatsAppClick = {
                                val msg = "Bonjour, je vous contacte concernant votre annonce sur Annonces Mhalma : ${ad.title}"
                                viewModel.authManager.openWhatsApp(ad.sellerWhatsApp, msg)
                            },
                            onDeleteClick = {
                                viewModel.deleteAd(ad)
                            }
                        )

                        // Insert AdMob Native Card every 3 ads if enabled
                        if (settings.admobEnabled && (index + 1) % 3 == 0) {
                            AdMobNativeCard()
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCreateDialog) {
        CreateAdDialog(
            currentCount = ads.size,
            maxLimit = settings.maxAdsLimit,
            onDismiss = { showCreateDialog = false },
            onPublish = { title, desc, price, cat, uri, neigh ->
                viewModel.publishAd(title, desc, price, cat, uri, neigh)
                showCreateDialog = false
            }
        )
    }

    selectedAdDetail?.let { ad ->
        val canDelete = isAdminLoggedIn || (currentUser != null && (currentUser?.userId == ad.sellerUserId || currentUser?.phone == ad.sellerPhone))
        AdDetailDialog(
            ad = ad,
            canDelete = canDelete,
            onDismiss = { selectedAdDetail = null },
            onWhatsAppClick = {
                val msg = "Bonjour, je vous contacte au sujet de votre annonce sur Annonces Mhalma : ${ad.title}"
                viewModel.authManager.openWhatsApp(ad.sellerWhatsApp, msg)
            },
            onCallClick = {
                viewModel.authManager.openDialer(ad.sellerPhone)
            },
            onDeleteClick = {
                viewModel.deleteAd(ad)
                selectedAdDetail = null
            }
        )
    }

    if (showAuthDialog) {
        WhatsAppAuthDialog(
            currentUser = currentUser,
            onDismiss = { showAuthDialog = false },
            onRegisterUser = { name, phone, neigh ->
                viewModel.createWhatsAppAccount(name, phone, neigh)
            },
            onOpenWhatsAppLink = { ph, msg ->
                viewModel.authManager.openWhatsApp(ph, msg)
            },
            onOpenAdminLogin = {
                showAuthDialog = false
                showAdminDialog = true
            }
        )
    }

    if (showAdminDialog) {
        AdminDialog(
            isAdminLoggedIn = isAdminLoggedIn,
            settings = settings,
            adsList = allAdsAdmin,
            onDismiss = { showAdminDialog = false },
            onLogin = { u, p -> viewModel.loginAdmin(u, p) },
            onLogout = { viewModel.logoutAdmin() },
            onUpdateGpsRadius = { r -> viewModel.updateGpsRadius(r) },
            onUpdateAdmob = { en -> viewModel.updateAdmobEnabled(en) },
            onToggleDemoLocation = { d -> viewModel.toggleDemoLocation(d) },
            onPurgeExpired = { viewModel.purgeExpiredAds() },
            onDeleteAd = { ad -> viewModel.deleteAd(ad) }
        )
    }
}
