package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.local.SessionManager

class LoginViewModel : ViewModel() {
    var correoElectronico by mutableStateOf("")
    var contrasenia by mutableStateOf("")

    fun onCorreoElectronicoChange(newValue: String) {
        correoElectronico = newValue
    }

    fun onContraseniaChange(newValue: String) {
        contrasenia = newValue
    }

    fun login(context: Context, onSuccess: () -> Unit) {
        if (correoElectronico.isNotEmpty() && contrasenia.isNotEmpty()) {

            // Guardamos el ID en SharedPreferences
            val sessionManager = SessionManager(context)
            // Usamos un ID de prueba
            sessionManager.saveUserId("user123")

            onSuccess()
        }
    }
}
