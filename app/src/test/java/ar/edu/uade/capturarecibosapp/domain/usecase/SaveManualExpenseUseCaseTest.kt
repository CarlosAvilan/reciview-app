package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SaveManualExpenseUseCaseTest {

    private lateinit var ticketRepository: TicketRepository
    private lateinit var useCase: SaveManualExpenseUseCase

    private val validCategories = listOf(
        UserCategory(id = 1L, name = "Comida", icon = "🍔", budget = 5000.0, userId = "user1")
    )

    @Before
    fun setUp() {
        ticketRepository = mockk()
        useCase = SaveManualExpenseUseCase(ticketRepository)
    }

    @Test
    fun `blank amount returns ValidationError with montoError`() = runTest {
        val result = useCase("", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun `non-numeric amount returns ValidationError with montoError`() = runTest {
        val result = useCase("abc", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun `zero amount returns ValidationError with montoError`() = runTest {
        val result = useCase("0", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun `negative amount returns ValidationError with montoError`() = runTest {
        val result = useCase("-100", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun `blank establishment returns ValidationError with establecimientoError`() = runTest {
        val result = useCase("500", "  ", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).establecimientoError)
    }

    @Test
    fun `blank category returns ValidationError with categoriaError`() = runTest {
        val result = useCase("500", "Supermercado", "", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).categoriaError)
    }

    @Test
    fun `multiple invalid fields report all errors at once`() = runTest {
        val result = useCase("", "", "", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        val error = result as SaveManualExpenseUseCase.Result.ValidationError
        assertNotNull(error.montoError)
        assertNotNull(error.establecimientoError)
        assertNotNull(error.categoriaError)
    }

    @Test
    fun `valid data calls repository and returns Success`() = runTest {
        coEvery { ticketRepository.saveTicket(any()) } returns Result.success(Unit)
        val result = useCase("500", "Supermercado", "Comida", "compras del mes", "15/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.Success)
    }

    @Test
    fun `UI date dd-MM-yyyy is converted to API format yyyy-MM-dd`() = runTest {
        var capturedDate = ""
        coEvery { ticketRepository.saveTicket(any()) } answers {
            capturedDate = (args[0] as Ticket).createdAt
            Result.success(Unit)
        }

        useCase("100", "Farmacia", "Comida", "", "25/12/2024", "user1", validCategories)

        assertEquals("2024-12-25", capturedDate)
    }

    @Test
    fun `category id is resolved from the categories list`() = runTest {
        var capturedCategoryId: Long? = null
        coEvery { ticketRepository.saveTicket(any()) } answers {
            capturedCategoryId = (args[0] as Ticket).categoryId
            Result.success(Unit)
        }

        useCase("200", "Kiosco", "Comida", "", "01/06/2024", "user1", validCategories)

        assertEquals(1L, capturedCategoryId)
    }

    @Test
    fun `repository failure returns Failure`() = runTest {
        coEvery { ticketRepository.saveTicket(any()) } returns Result.failure(Exception("DB error"))
        val result = useCase("500", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.Failure)
    }
}