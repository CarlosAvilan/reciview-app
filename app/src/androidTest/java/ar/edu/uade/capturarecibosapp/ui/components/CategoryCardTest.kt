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

    // Valores menores a 1000 para evitar separadores de miles dependientes del locale
    private val categoryNormal = CategoryItem(
        icon = "🍔",
        name = "Comida",
        spent = 500.0,
        budget = 800.0
    )

    private val categoryOverBudget = CategoryItem(
        icon = "🚗",
        name = "Transporte",
        spent = 900.0,
        budget = 800.0
    )

    @Test
    fun categoryCard_displaysName() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Comida").assertIsDisplayed()
    }

    @Test
    fun categoryCard_displaysIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("🍔").assertIsDisplayed()
    }

    @Test
    fun categoryCard_displaysSpentAndBudgetAmounts() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryNormal, onClick = {})
            }
        }

        // El formato es "$500 / $800" para valores sin separador de miles
        composeTestRule.onNodeWithText("$500 / $800").assertIsDisplayed()
    }

    @Test
    fun categoryCard_clickTriggers_onClickCallback() {
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
    fun categoryCard_overBudget_displaysName() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryOverBudget, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Transporte").assertIsDisplayed()
    }

    @Test
    fun categoryCard_overBudget_displaysAmounts() {
        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryOverBudget, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("$900 / $800").assertIsDisplayed()
    }

    @Test
    fun categoryCard_overBudget_clickStillWorks() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryOverBudget, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Transporte").performClick()

        assertTrue(clicked)
    }

    @Test
    fun categoryCard_zeroBudget_displaysWithoutCrash() {
        val categoryZeroBudget = CategoryItem(
            icon = "📁",
            name = "Sin límite",
            spent = 100.0,
            budget = 0.0
        )

        composeTestRule.setContent {
            MaterialTheme {
                CategoryCard(category = categoryZeroBudget, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Sin límite").assertIsDisplayed()
    }
}