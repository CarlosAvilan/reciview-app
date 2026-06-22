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
    fun HelpViewModelTest_Tips_TieneDosItems() {
        assertEquals(2, viewModel.tips.size)
    }

    @Test
    fun HelpViewModelTest_Faqs_TieneCuatroItems() {
        assertEquals(4, viewModel.faqs.size)
    }

    @Test
    fun HelpViewModelTest_Faqs_ComienzanCerradas() {
        assertTrue(viewModel.faqs.all { !it.isExpanded })
    }

    @Test
    fun HelpViewModelTest_ToggleFaq_ExpandeFaq() {
        viewModel.toggleFaq(0)
        assertTrue(viewModel.faqs[0].isExpanded)
    }

    @Test
    fun HelpViewModelTest_ToggleFaq_ColapsaFaqExpandida() {
        viewModel.toggleFaq(1)
        viewModel.toggleFaq(1)
        assertFalse(viewModel.faqs[1].isExpanded)
    }

    @Test
    fun HelpViewModelTest_ToggleFaq_NoAfectaOtrasFaqs() {
        viewModel.toggleFaq(0)
        assertFalse(viewModel.faqs[1].isExpanded)
        assertFalse(viewModel.faqs[2].isExpanded)
        assertFalse(viewModel.faqs[3].isExpanded)
    }

    @Test
    fun HelpViewModelTest_Tips_TienenContenido() {
        viewModel.tips.forEach { tip ->
            assertTrue(tip.title.isNotBlank())
            assertTrue(tip.description.isNotBlank())
        }
    }

    @Test
    fun HelpViewModelTest_Faqs_TienenContenido() {
        viewModel.faqs.forEach { faq ->
            assertTrue(faq.question.isNotBlank())
            assertTrue(faq.answer.isNotBlank())
        }
    }
}