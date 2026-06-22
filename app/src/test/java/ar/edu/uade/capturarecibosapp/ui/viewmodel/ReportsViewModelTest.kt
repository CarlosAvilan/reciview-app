package ar.edu.uade.capturarecibosapp.ui.viewmodel

import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReportsViewModelTest {

    private lateinit var viewModel: ReportsViewModel
    private val MAY_TOTAL = 44446.97f
    private val JUN_TOTAL = 33660.0f

    @Before
    fun setUp() {
        viewModel = ReportsViewModel()
    }

    // Estado inicial

    @Test
    fun ReportsViewModelTest_MonthlyEvolution_NoEstaVacio() {
        assertTrue(viewModel.monthlyEvolution.isNotEmpty())
    }

    @Test
    fun ReportsViewModelTest_MonthlyEvolution_TieneDosReportes() {
        assertEquals(2, viewModel.monthlyEvolution.size)
    }

    @Test
    fun ReportsViewModelTest_PrimerReporte_EsMayo() {
        assertEquals("MAY", viewModel.monthlyEvolution[0].month)
    }

    @Test
    fun ReportsViewModelTest_SegundoReporte_EsJunio() {
        assertEquals("JUN", viewModel.monthlyEvolution[1].month)
    }

    @Test
    fun ReportsViewModelTest_TotalMayo_CoincideConTickets() {
        val mayReport = viewModel.monthlyEvolution[0]
        assertEquals(MAY_TOTAL, mayReport.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_TotalJunio_CoincideConGastos() {
        val junReport = viewModel.monthlyEvolution[1]
        assertEquals(JUN_TOTAL, junReport.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_SelectedReport_EsUltimoPorDefecto() {
        assertEquals("JUN", viewModel.selectedReport.month)
    }

    // onReportSelected

    @Test
    fun ReportsViewModelTest_OnReportSelected_ActualizaSeleccion() {
        val mayReport = viewModel.monthlyEvolution[0]
        viewModel.onReportSelected(mayReport)
        assertEquals("MAY", viewModel.selectedReport.month)
    }

    @Test
    fun ReportsViewModelTest_OnReportSelected_MantieneObjetoCustom() {
        val custom = MonthlyReport(month = "DIC", amount = 999f, averageCost = "$999", mostActiveDay = "Lunes")
        viewModel.onReportSelected(custom)
        assertEquals("DIC", viewModel.selectedReport.month)
        assertEquals(999f, viewModel.selectedReport.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_SeleccionarDosVeces_MantieneReporte() {
        val jun = viewModel.monthlyEvolution[1]
        viewModel.onReportSelected(jun)
        viewModel.onReportSelected(jun)
        assertEquals("JUN", viewModel.selectedReport.month)
    }

    @Test
    fun ReportsViewModelTest_MonthlyEvolution_NoCambiaConSeleccion() {
        val original = viewModel.monthlyEvolution.toList()
        viewModel.onReportSelected(viewModel.monthlyEvolution[0])
        assertEquals(original, viewModel.monthlyEvolution)
    }
}