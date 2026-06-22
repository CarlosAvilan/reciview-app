package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statCard_displaysTitle() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Promedio por gasto", value = "$1.234")
            }
        }

        composeTestRule.onNodeWithText("Promedio por gasto").assertIsDisplayed()
    }

    @Test
    fun statCard_displaysValue() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Promedio por gasto", value = "$1.234")
            }
        }

        composeTestRule.onNodeWithText("$1.234").assertIsDisplayed()
    }

    @Test
    fun statCard_displaysTitleAndValueSimultaneously() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Día más activo", value = "Lunes")
            }
        }

        composeTestRule.onNodeWithText("Día más activo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunes").assertIsDisplayed()
    }

    @Test
    fun statCard_displaysEmptyStrings() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "", value = "")
            }
        }

        composeTestRule.onNodeWithText("", substring = false).assertIsDisplayed()
    }

    @Test
    fun statCard_displaysLongText() {
        val longTitle = "Este es un título muy largo que podría truncarse en pantallas pequeñas"
        val longValue = "Jueves"

        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = longTitle, value = longValue)
            }
        }

        composeTestRule.onNodeWithText(longTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(longValue).assertIsDisplayed()
    }
}