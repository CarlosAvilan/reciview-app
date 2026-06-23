package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.data.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SaveCategoryUseCaseTest {

    private lateinit var categoryRepository: CategoryRepository
    private lateinit var useCase: SaveCategoryUseCase

    @Before
    fun setUp() {
        categoryRepository = mockk()
        useCase = SaveCategoryUseCase(categoryRepository)
    }

    @Test
    fun SaveCategoryUseCaseTest_NombreEnBlanco_RetornaErrorDeValidacionConNombreErrorVerdadero() = runTest {
        val result = useCase("  ", "1000", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        val error = result as SaveCategoryUseCase.Result.ValidationError
        assertTrue(error.nameError)
        assertFalse(error.budgetError)
    }

    @Test
    fun SaveCategoryUseCaseTest_PresupuestoNoNumerico_RetornaErrorDeValidacionConPresupuestoErrorVerdadero() = runTest {
        val result = useCase("Comida", "abc", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        val error = result as SaveCategoryUseCase.Result.ValidationError
        assertFalse(error.nameError)
        assertTrue(error.budgetError)
    }

    @Test
    fun SaveCategoryUseCaseTest_PresupuestoNegativo_RetornaErrorDeValidacionConPresupuestoErrorVerdadero() = runTest {
        val result = useCase("Comida", "-500", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        assertTrue((result as SaveCategoryUseCase.Result.ValidationError).budgetError)
    }

    @Test
    fun SaveCategoryUseCaseTest_DatosValidos_CreaNuevaCategoriaYRetornaExito() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Transporte", "5000", "🚗", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
    }

    @Test
    fun SaveCategoryUseCaseTest_DatosValidosConCategoriaExistente_LaActualizaPreservandoElIdOriginal() = runTest {
        val existing = UserCategory(id = 10L, name = "Viejo", icon = "📁", budget = 1000.0, userId = "user1")
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)

        val result = useCase("Nuevo", "2000", "🏠", "user1", existingCategory = existing)

        assertTrue(result is SaveCategoryUseCase.Result.Success)
        val saved = (result as SaveCategoryUseCase.Result.Success).category
        assertEquals("Nuevo", saved.name)
        assertEquals(2000.0, saved.budget, 0.01)
        assertEquals("🏠", saved.icon)
        assertEquals(10L, saved.id)
    }

    @Test
    fun SaveCategoryUseCaseTest_FormatoConPuntoYComa_EsParseadoCorrectamente() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Comida", "1.234,56", "🍔", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
        assertEquals(1234.56, (result as SaveCategoryUseCase.Result.Success).category.budget, 0.01)
    }

    @Test
    fun SaveCategoryUseCaseTest_PresupuestoConSignoPeso_EsParseadoCorrectamente() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Salud", "\$5000", "💊", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
        assertEquals(5000.0, (result as SaveCategoryUseCase.Result.Success).category.budget, 0.01)
    }

    @Test
    fun SaveCategoryUseCaseTest_PresupuestoEnCero_EsValidoYRetornaExito() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Sin límite", "0", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
    }

    @Test
    fun SaveCategoryUseCaseTest_FalloDelRepositorio_RetornaFallo() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.failure(Exception("DB error"))
        val result = useCase("Comida", "1000", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Failure)
    }
}