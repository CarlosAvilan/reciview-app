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
    fun SaveManualExpenseUseCaseTest_MontoEnBlanco_RetornaErrorDeMonto() = runTest {
        val result = useCase("", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_MontoNoNumerico_RetornaErrorDeMonto() = runTest {
        val result = useCase("abc", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_MontoEnCero_RetornaErrorDeMonto() = runTest {
        val result = useCase("0", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_MontoNegativo_RetornaErrorDeMonto() = runTest {
        val result = useCase("-100", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).montoError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_EstablecimientoEnBlanco_RetornaErrorDeEstablecimiento() = runTest {
        val result = useCase("500", "  ", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).establecimientoError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_CategoriaEnBlanco_RetornaErrorDeCategoria() = runTest {
        val result = useCase("500", "Supermercado", "", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        assertNotNull((result as SaveManualExpenseUseCase.Result.ValidationError).categoriaError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_MultiplesCamposInvalidos_ReportaTodosLosErrores() = runTest {
        val result = useCase("", "", "", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.ValidationError)
        val error = result as SaveManualExpenseUseCase.Result.ValidationError
        assertNotNull(error.montoError)
        assertNotNull(error.establecimientoError)
        assertNotNull(error.categoriaError)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_DatosValidos_LlamaAlRepositorioYRetornaExito() = runTest {
        coEvery { ticketRepository.saveTicket(any()) } returns Result.success(Unit)
        val result = useCase("500", "Supermercado", "Comida", "compras del mes", "15/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.Success)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_FechaDeUi_EsConvertidaAFormatoApiAnioMesDia() = runTest {
        var capturedDate = ""
        coEvery { ticketRepository.saveTicket(any()) } answers {
            capturedDate = (args[0] as Ticket).createdAt
            Result.success(Unit)
        }

        useCase("100", "Farmacia", "Comida", "", "25/12/2024", "user1", validCategories)
        assertEquals("2024-12-25", capturedDate)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_IdDeCategoria_EsResueltoDesdeLaListaDeCategorias() = runTest {
        var capturedCategoryId: Long? = null
        coEvery { ticketRepository.saveTicket(any()) } answers {
            capturedCategoryId = (args[0] as Ticket).categoryId
            Result.success(Unit)
        }

        useCase("200", "Kiosco", "Comida", "", "01/06/2024", "user1", validCategories)

        assertEquals(1L, capturedCategoryId)
    }

    @Test
    fun SaveManualExpenseUseCaseTest_FalloDelRepositorio_RetornaFallo() = runTest {
        coEvery { ticketRepository.saveTicket(any()) } returns Result.failure(Exception("error"))
        val result = useCase("500", "Supermercado", "Comida", "", "01/06/2024", "user1", validCategories)
        assertTrue(result is SaveManualExpenseUseCase.Result.Failure)
    }
}