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
    fun `blank name returns ValidationError with nameError true`() = runTest {
        val result = useCase("  ", "1000", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        val error = result as SaveCategoryUseCase.Result.ValidationError
        assertTrue(error.nameError)
        assertFalse(error.budgetError)
    }

    @Test
    fun `non-numeric budget returns ValidationError with budgetError true`() = runTest {
        val result = useCase("Comida", "abc", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        val error = result as SaveCategoryUseCase.Result.ValidationError
        assertFalse(error.nameError)
        assertTrue(error.budgetError)
    }

    @Test
    fun `negative budget returns ValidationError with budgetError true`() = runTest {
        val result = useCase("Comida", "-500", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.ValidationError)
        assertTrue((result as SaveCategoryUseCase.Result.ValidationError).budgetError)
    }

    @Test
    fun `valid data creates new category and returns Success`() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Transporte", "5000", "🚗", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
    }

    @Test
    fun `valid data with existing category updates it preserving the original id`() = runTest {
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
    fun `Argentine format 1 dot 234 comma 56 is parsed as 1234 point 56`() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Comida", "1.234,56", "🍔", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
        assertEquals(1234.56, (result as SaveCategoryUseCase.Result.Success).category.budget, 0.01)
    }

    @Test
    fun `budget with peso sign is parsed correctly`() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Salud", "\$5000", "💊", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
        assertEquals(5000.0, (result as SaveCategoryUseCase.Result.Success).category.budget, 0.01)
    }

    @Test
    fun `zero budget is valid`() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.success(Unit)
        val result = useCase("Sin límite", "0", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Success)
    }

    @Test
    fun `repository failure returns Failure`() = runTest {
        coEvery { categoryRepository.saveCategory(any()) } returns Result.failure(Exception("DB error"))
        val result = useCase("Comida", "1000", "📁", "user1")
        assertTrue(result is SaveCategoryUseCase.Result.Failure)
    }
}