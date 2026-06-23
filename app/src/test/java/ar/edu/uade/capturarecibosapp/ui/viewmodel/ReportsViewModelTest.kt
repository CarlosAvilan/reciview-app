package ar.edu.uade.capturarecibosapp.ui.viewmodel

import ar.edu.uade.capturarecibosapp.data.local.seeders.ExpenseSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.TicketSeeder
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReportsViewModelMockTest {
    private val mockTickets = listOf(
        Ticket(
            id = 1,
            createdAt = "2024-01-10",
            userId = "user1",
            amount = 1000f,
            establishment = "Test Ticket",
            categoryId = 1,
            photoUrl = ""
        )
    )

    private val mockExpenses = listOf(
        ExpenseItem(
            id = 1,
            date = "2024-01-15",
            userId = "user1",
            amount = 500.0,
            title = "Test Expense",
            photoUrl = 21,
            category = "1"
        )
    )

    private lateinit var viewModel: ReportsViewModel

    @Before
    fun setUp() {
        // Interceptamos los constructores de los seeders
        mockkConstructor(TicketSeeder::class)
        mockkConstructor(ExpenseSeeder::class)

        // Definimos qué devolver cuando el ViewModel llame a los seeders
        every { anyConstructed<TicketSeeder>().provideInitialTickets() } returns mockTickets
        every { anyConstructed<ExpenseSeeder>().provideInitialExpenses() } returns mockExpenses

        viewModel = ReportsViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun ReportsViewModelTest_CalculaDatosMockCorrectamente() {
        // ENE 2024: 1000 (ticket) + 500 (gasto) = 1500
        val report = viewModel.monthlyEvolution[0]
        assertEquals("ENE", report.month)
        assertEquals(1500f, report.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_MonthlyEvolution_TieneUnSoloReporte() {
        // Como solo enviamos datos de Enero, debería haber solo 1
        assertEquals(1, viewModel.monthlyEvolution.size)
    }

    @Test
    fun ReportsViewModelTest_SelectedReport_EsElMocked() {
        assertEquals("ENE", viewModel.selectedReport.month)
        assertEquals(1500f, viewModel.selectedReport.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_OnReportSelected_FuncionaConDatosNuevos() {
        val custom = MonthlyReport(month = "FEB", amount = 200f, averageCost = "$200", mostActiveDay = "Martes")
        viewModel.onReportSelected(custom)
        assertEquals("FEB", viewModel.selectedReport.month)
    }

}
