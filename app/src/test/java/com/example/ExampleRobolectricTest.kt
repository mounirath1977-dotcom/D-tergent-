package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.auth.AuthManager
import com.example.model.AdItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Annonces Mhalma", appName)
  }

  @Test
  fun `test secret admin trigger`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val auth = AuthManager(context)

    assertTrue(auth.isSecretTrigger("mounirath@yahoo.fr"))
    assertTrue(auth.isSecretTrigger("  mounirath@yahoo.fr  "))
    assertFalse(auth.isSecretTrigger("other@yahoo.fr"))

    // Test credentials
    assertTrue(auth.checkAdminCredentials("dziri-diou", "212154"))
    assertFalse(auth.checkAdminCredentials("dziri-diou", "wrong_pass"))
  }

  @Test
  fun `test ad 7-day auto-expiration logic`() {
    val now = System.currentTimeMillis()
    val activeAd = AdItem(
      id = 1,
      title = "F3 Mhalma",
      description = "Appartement F3",
      price = 40000.0,
      category = "Immobilier",
      imageUri = "",
      sellerName = "Ahmed",
      sellerPhone = "0550123456",
      sellerWhatsApp = "213550123456",
      sellerUserId = "u1",
      createdAt = now - TimeUnit.DAYS.toMillis(2)
    )
    assertFalse(activeAd.isExpired(now))

    val expiredAd = AdItem(
      id = 2,
      title = "Golf 7",
      description = "Voiture",
      price = 2500000.0,
      category = "Véhicules",
      imageUri = "",
      sellerName = "Sofiane",
      sellerPhone = "0550123456",
      sellerWhatsApp = "213550123456",
      sellerUserId = "u2",
      createdAt = now - TimeUnit.DAYS.toMillis(8) // 8 days old
    )
    assertTrue(expiredAd.isExpired(now))
  }
}

