package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var nombreCompleto by mutableStateOf("")
    var correoElectronico by mutableStateOf("")
    var telefono by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var paisResidencia by mutableStateOf("")

    fun onNombreChange(newValue: String) {
        nombreCompleto = newValue
    }

    fun onCorreoChange(newValue: String) {
        correoElectronico = newValue
    }

    fun onTelefonoChange(newValue: String) {
        telefono = newValue
    }

    fun onFechaNacimientoChange(newValue: String) {
        fechaNacimiento = newValue
    }

    fun onPaisChange(newValue: String) {
        paisResidencia = newValue
    }

    fun registrarse() {
        // Lógica de registro a implementar a futuro
    }
}
