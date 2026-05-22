package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var correoElectronico by mutableStateOf("")
    var contrasenia by mutableStateOf("")

    fun onCorreoElectronicoChange(newValue: String) {
        correoElectronico = newValue
    }

    fun onContraseniaChange(newValue: String) {
        contrasenia = newValue
    }

    fun login() {
        // TODO
    }
}