package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaqItemRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val question = "¿Cómo exporto mis gastos?"
    private val answer = "Podés exportar tus reportes mensuales desde la sección Reportes."

    @Test
    fun faqItemRow_questionAlwaysVisible_whenCollapsed() {
        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = false,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(question).assertIsDisplayed()
    }

    @Test
    fun faqItemRow_questionAlwaysVisible_whenExpanded() {
        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = true,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(question).assertIsDisplayed()
    }

    @Test
    fun faqItemRow_answerHidden_whenCollapsed() {
        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = false,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(answer).assertIsNotDisplayed()
    }

    @Test
    fun faqItemRow_answerVisible_whenExpanded() {
        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = true,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(answer).assertIsDisplayed()
    }

    @Test
    fun faqItemRow_clickTriggers_onClickCallback() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = false,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText(question).performClick()

        assertTrue(clicked)
    }

    @Test
    fun faqItemRow_clickOnAnswer_triggerCallback_whenExpanded() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                FaqItemRow(
                    question = question,
                    answer = answer,
                    isExpanded = true,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText(answer).performClick()

        assertTrue(clicked)
    }
}