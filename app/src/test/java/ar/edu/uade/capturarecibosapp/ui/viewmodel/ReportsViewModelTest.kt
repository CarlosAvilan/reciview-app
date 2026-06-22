package ar.edu.uade.capturarecibosapp.ui.viewmodel

import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReportsViewModelTest {

    private lateinit var viewModel: ReportsViewModel

    // Totales calculados a partir de los datos del seeder:
    // TicketSeeder  → 5 tickets en 2026-05 → total 44.446,97
    // ExpenseSeeder → 5 gastos  en 2026-06 → total 33.660,00
    private val MAY_TOTAL = 44446.97f
    private val JUN_TOTAL = 33660.0f

    @Before
    fun setUp() {
        viewModel = ReportsViewModel()
    }

    // ── Estado inicial ──────────────────────────────────────────────────────

    @Test
    fun `monthlyEvolution is not empty`() {
        assertTrue(viewModel.monthlyEvolution.isNotEmpty())
    }

    @Test
    fun `monthlyEvolution has two reports (may and june)`() {
        assertEquals(2, viewModel.monthlyEvolution.size)
    }

    @Test
    fun `monthlyEvolution first report is MAY`() {
        assertEquals("MAY", viewModel.monthlyEvolution[0].month)
    }

    @Test
    fun `monthlyEvolution second report is JUN`() {
        assertEquals("JUN", viewModel.monthlyEvolution[1].month)
    }

    @Test
    fun `MAY total matches sum of ticket seeder amounts`() {
        val mayReport = viewModel.monthlyEvolution[0]
        assertEquals(MAY_TOTAL, mayReport.amount, 0.01f)
    }

    @Test
    fun `JUN total matches sum of expense seeder amounts`() {
        val junReport = viewModel.monthlyEvolution[1]
        assertEquals(JUN_TOTAL, junReport.amount, 0.01f)
    }

    @Test
    fun `selectedReport initially is the last report (JUN)`() {
        assertEquals("JUN", viewModel.selectedReport.month)
    }

    // ── onReportSelected ────────────────────────────────────────────────────

    @Test
    fun `onReportSelected updates selectedReport`() {
        val mayReport = viewModel.monthlyEvolution[0]
        viewModel.onReportSelected(mayReport)
        assertEquals("MAY", viewModel.selectedReport.month)
    }

    @Test
    fun `onReportSelected with a new object keeps the provided report`() {
        val custom = MonthlyReport(month = "DIC", amount = 999f, averageCost = "$999", mostActiveDay = "Lunes")
        viewModel.onReportSelected(custom)
        assertEquals("DIC", viewModel.selectedReport.month)
        assertEquals(999f, viewModel.selectedReport.amount, 0.01f)
    }

    @Test
    fun `selecting the same report twice keeps that report`() {
        val jun = viewModel.monthlyEvolution[1]
        viewModel.onReportSelected(jun)
        viewModel.onReportSelected(jun)
        assertEquals("JUN", viewModel.selectedReport.month)
    }

    @Test
    fun `monthlyEvolution does not change after onReportSelected`() {
        val original = viewModel.monthlyEvolution.toList()
        viewModel.onReportSelected(viewModel.monthlyEvolution[0])
        assertEquals(original, viewModel.monthlyEvolution)
    }
}