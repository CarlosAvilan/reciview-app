package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
    fun StatCardTest_MuestraTitulo() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Promedio por gasto", value = "$1.234")
            }
        }

        composeTestRule.onNodeWithText("Promedio por gasto").assertIsDisplayed()
    }

    @Test
    fun StatCardTest_MuestraValor() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Promedio por gasto", value = "$1.234")
            }
        }

        composeTestRule.onNodeWithText("$1.234").assertIsDisplayed()
    }

    @Test
    fun StatCardTest_MuestraTituloYValor() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "Día más activo", value = "Lunes")
            }
        }

        composeTestRule.onNodeWithText("Día más activo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunes").assertIsDisplayed()
    }

    @Test
    fun StatCardTest_MuestraStringsVacios() {
        composeTestRule.setContent {
            MaterialTheme {
                StatCard(title = "", value = "")
            }
        }

        composeTestRule.onAllNodesWithText("", substring = false).assertCountEquals(2)
    }

    @Test
    fun StatCardTest_MuestraTextoLargo() {
        val longTitle = "Yo soy un título muy largo que podría no entrar en pantallas chicas"
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