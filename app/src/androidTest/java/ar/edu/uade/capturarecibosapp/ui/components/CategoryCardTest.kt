package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val categoryNormal = CategoryItem(
        icon = "🍔",
        name = "Comida",
        spent = 500.0,
        budget = 800.0
    )

    private val categorySobrePrecioLimite = CategoryItem(
        icon = "🚗",
        name = "Transporte",
        spent = 900.0,
        budget = 800.0
    )

    @Test
    fun categoryCard_muestraNombre() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Comida").assertIsDisplayed()
    }

    @Test
    fun categoryCard_muestraIcono() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("🍔").assertIsDisplayed()
    }

    @Test
    fun categoryCard_muestraGastadoYLimite() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("$500 / $800").assertIsDisplayed()
    }

    @Test
    fun categoryCard_llamaCallback() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Comida").performClick()

        assertTrue(clicked)
    }

    @Test
    fun categoryCard_siExcedeLimite_muestraNombre() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categorySobrePrecioLimite, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Transporte").assertIsDisplayed()
    }

    @Test
    fun categoryCard_siExcedeLimite_muestraTotal() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categorySobrePrecioLimite, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("$900 / $800").assertIsDisplayed()
    }

    @Test
    fun categoryCard_siExcedeLimite_funcionaNormalmente() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categorySobrePrecioLimite, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Transporte").performClick()
        assertTrue(clicked)
    }
}