package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.enums.ForgotPasswordStep
import ar.edu.uade.capturarecibosapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: AuthRepository = DependencyProvider.provideAuthRepository()
) : ViewModel() {

    var currentStep by mutableStateOf(ForgotPasswordStep.EMAIL)
        private set

    var email by mutableStateOf("")
    var code by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var repeatPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onEmailChange(newValue: String) {
        email = newValue
        errorMessage = null
    }

    fun onCodeChange(newValue: String) {
        if (newValue.length <= 6) {
            code = newValue
        }
        errorMessage = null
    }

    fun onNewPasswordChange(newValue: String) {
        newPassword = newValue
        errorMessage = null
    }

    fun onRepeatPasswordChange(newValue: String) {
        repeatPassword = newValue
        errorMessage = null
    }

    fun sendCode() {
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = "Ingresá un mail válido"
            return
        }

        isLoading = true
        viewModelScope.launch {
            val result = repository.sendRecoveryCode(email)
            isLoading = false
            if (result.isSuccess) {
                currentStep = ForgotPasswordStep.VERIFY_CODE
            } else {
                errorMessage = "Error al enviar el código"
            }
        }
    }

    fun verifyCode() {
        if (code.length != 6) {
            errorMessage = "El código debe ser de 6 dígitos"
            return
        }

        isLoading = true
        viewModelScope.launch {
            // Mocked verification
            val result = repository.resetPassword(email)
            isLoading = false
            if (result.isSuccess) {
                currentStep = ForgotPasswordStep.NEW_PASSWORD
            } else {
                errorMessage = "Código incorrecto"
            }
        }
    }

    fun resetPassword() {
        if (newPassword.isBlank()) {
            errorMessage = "La contraseña no puede estar vacía"
            return
        }
        if (newPassword != repeatPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        isLoading = true
        viewModelScope.launch {
            // Mocked final step
            val result = Result.success(Unit) 
            isLoading = false
            if (result.isSuccess) {
                currentStep = ForgotPasswordStep.SUCCESS
            } else {
                errorMessage = "Error al restablecer contraseña"
            }
        }
    }

    fun backToEmail() {
        currentStep = ForgotPasswordStep.EMAIL
        errorMessage = null
    }

    fun backToVerifyCode() {
        currentStep = ForgotPasswordStep.VERIFY_CODE
        errorMessage = null
    }
}
