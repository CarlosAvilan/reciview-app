package ar.edu.uade.capturarecibosapp.domain.usecase

import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import ar.edu.uade.capturarecibosapp.data.model.UserAuth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RegisterUserAuthUseCaseTest {

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
    fun RegisterUserUseCaseTest_ContraseniaMasCortaDeSeisCaracteres_RetornaErrorDeContrasenia() = runTest {
        val result = useCase(validEmail, "12345", validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.PasswordError)
    }

    @Test
    fun RegisterUserUseCaseTest_EmailVacio_RetornaErrorDeEmail() = runTest {
        val result = useCase("", validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun RegisterUserUseCaseTest_EmailSinArroba_RetornaErrorDeEmail() = runTest {
        val result = useCase("notanemail", validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun RegisterUserUseCaseTest_FechaNacimientoNula_RetornaErrorDeFechaNacimiento() = runTest {
        val result = useCase(validEmail, validPassword, validName, null, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun RegisterUserUseCaseTest_FechaNacimientoIgualAHoy_RetornaErrorDeFechaNacimiento() = runTest {
        val result = useCase(validEmail, validPassword, validName, LocalDate.now(), validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun RegisterUserUseCaseTest_FechaNacimientoFutura_RetornaErrorDeFechaNacimiento() = runTest {
        val result = useCase(validEmail, validPassword, validName, LocalDate.now().plusYears(1), validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.BirthDateError)
    }

    @Test
    fun RegisterUserUseCaseTest_DatosValidosYRepositorioExitoso_RetornaExitoConEmailCorrecto() = runTest {
        val userAuth = UserAuth(uuid = "uuid-123", email = validEmail)
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns Result.success(userAuth)

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)

        assertTrue(result is RegisterUserUseCase.Result.Success)
        assertEquals(validEmail, (result as RegisterUserUseCase.Result.Success).email)
    }

    @Test
    fun RegisterUserUseCaseTest_RepositorioFallaPorqueUsuarioYaExiste_RetornaErrorDeEmail() = runTest {
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns
            Result.failure(Exception("User already exists"))

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.EmailError)
    }

    @Test
    fun RegisterUserUseCaseTest_RepositorioFallaConErrorGenerico_RetornaFallo() = runTest {
        coEvery { authRepository.registerUser(any(), any(), any(), any(), any()) } returns
            Result.failure(Exception("Network error"))

        val result = useCase(validEmail, validPassword, validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.Failure)
    }

    @Test
    fun RegisterUserUseCaseTest_ContraseniaEsValidadaAntesQueElEmail_RetornaErrorDeContrasenia() = runTest {
        val result = useCase("bademail", "123", validName, validBirth, validCountry, true)
        assertTrue(result is RegisterUserUseCase.Result.PasswordError)
    }
}