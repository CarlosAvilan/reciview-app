package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.repository.TicketRepository // Asegúrate de importar tus interfaces/repositorios reales
import ar.edu.uade.capturarecibosapp.data.repository.ExpenseRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelMockTest {

    // 1. Definimos los mocks de los repositorios reales
    private val mockTicketRepository = mockk<TicketRepository>()
    private val mockExpenseRepository = mockk<ExpenseRepository>()

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
            photoUrl = "21",
            category = "1"
        )
    )

    private lateinit var viewModel: ReportsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(SessionManager)
        mockkObject(DependencyProvider)

        every { SessionManager.userId } returns "user1"
        every { DependencyProvider.provideTicketRepository(any()) } returns mockTicketRepository
        every { DependencyProvider.provideExpenseRepository(any()) } returns mockExpenseRepository
        every { mockTicketRepository.getTickets("user1") } returns flowOf(mockTickets)
        every { mockExpenseRepository.getExpensesForUser("user1") } returns flowOf(mockExpenses)

        viewModel = ReportsViewModel(mockkClass(Application::class))
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun ReportsViewModelTest_CalculaDatosMockCorrectamente() {
        assertFalse(viewModel.monthlyEvolution.isEmpty())
        val report = viewModel.monthlyEvolution[0]
        assertEquals("ENE", report.month)
        assertEquals(1500f, report.amount, 0.01f)
    }

    @Test
    fun ReportsViewModelTest_MonthlyEvolution_TieneUnSoloReporte() {
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