package ar.edu.uade.capturarecibosapp.ui.viewmodel

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HelpViewModelTest {

    private lateinit var viewModel: HelpViewModel

    @Before
    fun setUp() {
        viewModel = HelpViewModel()
    }

    @Test
    fun `tips list has two items`() {
        assertEquals(2, viewModel.tips.size)
    }

    @Test
    fun `faqs list has four items`() {
        assertEquals(4, viewModel.faqs.size)
    }

    @Test
    fun `all faqs start collapsed`() {
        assertTrue(viewModel.faqs.all { !it.isExpanded })
    }

    @Test
    fun `toggleFaq expands a collapsed faq`() {
        viewModel.toggleFaq(0)
        assertTrue(viewModel.faqs[0].isExpanded)
    }

    @Test
    fun `toggleFaq collapses an already expanded faq`() {
        viewModel.toggleFaq(1)
        viewModel.toggleFaq(1)
        assertFalse(viewModel.faqs[1].isExpanded)
    }

    @Test
    fun `toggling one faq does not affect the others`() {
        viewModel.toggleFaq(0)
        assertFalse(viewModel.faqs[1].isExpanded)
        assertFalse(viewModel.faqs[2].isExpanded)
        assertFalse(viewModel.faqs[3].isExpanded)
    }

    @Test
    fun `each tip has a non-empty title and description`() {
        viewModel.tips.forEach { tip ->
            assertTrue(tip.title.isNotBlank())
            assertTrue(tip.description.isNotBlank())
        }
    }

    @Test
    fun `each faq has a non-empty question and answer`() {
        viewModel.faqs.forEach { faq ->
            assertTrue(faq.question.isNotBlank())
            assertTrue(faq.answer.isNotBlank())
        }
    }
}