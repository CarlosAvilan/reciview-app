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
    fun ForgotPasswordViewModelTest_EstadoInicial_EsPasoEmailConCamposVacios() {
        assertEquals(ForgotPasswordStep.EMAIL, viewModel.currentStep)
        assertEquals("", viewModel.email)
        assertEquals("", viewModel.code)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_SendCodeEmailVacio_ProduceError() {
        viewModel.email = ""
        viewModel.sendCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_SendCodeEmailSinArroba_ProduceError() {
        viewModel.email = "notanemail"
        viewModel.sendCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnEmailChange_ActualizaEmailYLimpiaError() {
        viewModel.errorMessage = "algún error previo"
        viewModel.onEmailChange("nuevo@email.com")
        assertEquals("nuevo@email.com", viewModel.email)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_VerifyCodeCodigoCorto_ProduceError() {
        viewModel.code = "12345"
        viewModel.verifyCode()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnCodeChange_NoAceptaMasDeSeisCaracteres() {
        viewModel.onCodeChange("1234567")
        assertEquals("", viewModel.code)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnCodeChange_AceptaExactamenteSeisCaracteres() {
        viewModel.onCodeChange("123456")
        assertEquals("123456", viewModel.code)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnCodeChange_LimpiaError() {
        viewModel.errorMessage = "código incorrecto"
        viewModel.onCodeChange("1234")
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_ResetPasswordContraseniaVacia_ProduceError() {
        viewModel.newPassword = ""
        viewModel.repeatPassword = ""
        viewModel.resetPassword()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_ResetPasswordContraseniasDiferentes_ProduceError() {
        viewModel.newPassword = "password123"
        viewModel.repeatPassword = "different456"
        viewModel.resetPassword()
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_BackToEmail_ReseteaPasoYLimpiaError() {
        viewModel.errorMessage = "algún error"
        viewModel.backToEmail()
        assertEquals(ForgotPasswordStep.EMAIL, viewModel.currentStep)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_BackToVerifyCode_ReseteaPasoYLimpiaError() {
        viewModel.errorMessage = "algún error"
        viewModel.backToVerifyCode()
        assertEquals(ForgotPasswordStep.VERIFY_CODE, viewModel.currentStep)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnRepeatPasswordChange_ActualizaYLimpiaError() {
        viewModel.errorMessage = "las contraseñas no coinciden"
        viewModel.onRepeatPasswordChange("nuevapass")
        assertEquals("nuevapass", viewModel.repeatPassword)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun ForgotPasswordViewModelTest_OnNewPasswordChange_ActualizaYLimpiaError() {
        viewModel.errorMessage = "contraseña inválida"
        viewModel.onNewPasswordChange("nuevapass123")
        assertEquals("nuevapass123", viewModel.newPassword)
        assertNull(viewModel.errorMessage)
    }
}