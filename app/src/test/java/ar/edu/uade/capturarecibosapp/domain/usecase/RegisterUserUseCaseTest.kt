package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import ar.edu.uade.capturarecibosapp.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RegisterUserUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: RegisterUserUseCase

    private val validEmail = "juan@gmail.com"
    private val validPassword = "password123"
    private val validName = "Juan Pérez"
    private val validBirth = LocalDate.of(2000, 1, 1)
    private val validCountry = "AR"

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = RegisterUserUseCase(authRepository)
    }

    @Test
    fun `password shorter than 6 characters returns PasswordError`() = runTest {
        val result = useCase(validEmail, "12345", validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.PasswordError)
    }

    @Test
    fun `empty email returns EmailError`() = runTest {
        val result = useCase("", validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun `email without at-sign returns EmailError`() = runTest {
        val result = useCase("notanemail", validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun `null birth date returns BirthDateError`() = runTest {
        val result = useCase(validEmail, validPassword, validName, null, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun `birth date set to today returns BirthDateError`() = runTest {
        val result = useCase(validEmail, validPassword, validName, LocalDate.now(), validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun `future birth date returns BirthDateError`() = runTest {
        val result = useCase(validEmail, validPassword, validName, LocalDate.now().plusYears(1), validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun `valid data with repository success returns Success with correct email`() = runTest {
        val user = User(uuid = "uuid-123", email = validEmail)
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns Result.success(user)

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)

        assertTrue(result is RegisterUserUseCase.Result.Success)
        assertEquals(validEmail, (result as RegisterUserUseCase.Result.Success).email)
    }

    @Test
    fun `repository failure with already exists message returns EmailError`() = runTest {
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns
            Result.failure(Exception("User already exists"))

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun `repository failure with generic error returns Failure`() = runTest {
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns
            Result.failure(Exception("Network error"))

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.Failure)
    }

    @Test
    fun `password is validated before email`() = runTest {
        // Short password combined with invalid email should return PasswordError (first check)
        val result = useCase("bademail", "123", validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.PasswordError)
    }
}