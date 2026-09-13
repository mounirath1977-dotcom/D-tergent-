package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.AdItem
import com.example.ui.components.AdCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleAd = AdItem(
      id = 1,
      title = "Appartement F3 AADL Mhalma",
      description = "Bel appartement avec vue dégagée, refait à neuf",
      price = 45000.0,
      category = "Immobilier",
      imageUri = "",
      sellerName = "Karim Benali",
      sellerPhone = "0550123456",
      sellerWhatsApp = "213550123456",
      sellerUserId = "user1",
      neighborhood = "Cité 1500 logts AADL",
      createdAt = System.currentTimeMillis()
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        AdCard(
          ad = sampleAd,
          canDelete = true,
          onClick = {},
          onWhatsAppClick = {},
          onDeleteClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

