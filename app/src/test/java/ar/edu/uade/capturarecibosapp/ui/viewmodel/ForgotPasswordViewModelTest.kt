package ar.edu.uade.capturarecibosapp.ui.viewmodel

import ar.edu.uade.capturarecibosapp.data.enums.ForgotPasswordStep
import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ForgotPasswordViewModelTest {

    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        val mockRepo = mockk<AuthRepository>(relaxed = true)
        viewModel = ForgotPasswordViewModel(repository = mockRepo)
    }

    @Test
    fun `initial state is EMAIL step with blank fields and no error`() {
        assertEquals(ForgotPasswordStep.EMAIL, viewModel.currentStep)
        assertEquals("", viewModel.email)
        assertEquals("", viewModel.code)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `sendCode with blank email sets errorMessage`() {
        viewModel.email = ""
        viewModel.sendCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun `sendCode with email without at-sign sets errorMessage`() {
        viewModel.email = "notanemail"
        viewModel.sendCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun `onEmailChange updates email and clears previous errorMessage`() {
        viewModel.errorMessage = "algún error previo"
        viewModel.onEmailChange("nuevo@email.com")
        assertEquals("nuevo@email.com", viewModel.email)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `verifyCode with code shorter than 6 digits sets errorMessage`() {
        viewModel.code = "12345"
        viewModel.verifyCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun `onCodeChange does not accept more than 6 characters`() {
        viewModel.onCodeChange("1234567")
        assertEquals("", viewModel.code)
    }

    @Test
    fun `onCodeChange with exactly 6 characters is accepted`() {
        viewModel.onCodeChange("123456")
        assertEquals("123456", viewModel.code)
    }

    @Test
    fun `onCodeChange clears errorMessage`() {
        viewModel.errorMessage = "código incorrecto"
        viewModel.onCodeChange("1234")
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `resetPassword with blank new password sets errorMessage`() {
        viewModel.newPassword = ""
        viewModel.repeatPassword = ""
        viewModel.resetPassword()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun `resetPassword with mismatched passwords sets errorMessage`() {
        viewModel.newPassword = "password123"
        viewModel.repeatPassword = "different456"
        viewModel.resetPassword()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun `backToEmail resets step to EMAIL and clears errorMessage`() {
        viewModel.errorMessage = "algún error"
        viewModel.backToEmail()
        assertEquals(ForgotPasswordStep.EMAIL, viewModel.currentStep)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `backToVerifyCode sets step to VERIFY_CODE and clears errorMessage`() {
        viewModel.errorMessage = "algún error"
        viewModel.backToVerifyCode()
        assertEquals(ForgotPasswordStep.VERIFY_CODE, viewModel.currentStep)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `onRepeatPasswordChange updates field and clears errorMessage`() {
        viewModel.errorMessage = "las contraseñas no coinciden"
        viewModel.onRepeatPasswordChange("nuevapass")
        assertEquals("nuevapass", viewModel.repeatPassword)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `onNewPasswordChange updates field and clears errorMessage`() {
        viewModel.errorMessage = "contraseña inválida"
        viewModel.onNewPasswordChange("nuevapass123")
        assertEquals("nuevapass123", viewModel.newPassword)
        assertNull(viewModel.errorMessage)
    }
}