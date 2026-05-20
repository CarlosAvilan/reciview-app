package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PersonalInfoViewModel : ViewModel() {
    var nombre by mutableStateOf("Juan Pérez")
    var email by mutableStateOf("juanp@email.com")
    var telefono by mutableStateOf("+55 11 5555-5555")
    var fechaNacimiento by mutableStateOf("15 / 10 / 1990")
    var paisResidencia by mutableStateOf("Argentina")

    fun onNombreChange(newValue: String) { nombre = newValue }
    fun onEmailChange(newValue: String) { email = newValue }
    fun onTelefonoChange(newValue: String) { telefono = newValue }
    fun onFechaNacimientoChange(newValue: String) { fechaNacimiento = newValue }
    fun onPaisChange(newValue: String) { paisResidencia = newValue }

    fun guardarCambios() {
        // Lógica para guardar cambios
    }

    fun eliminarCuenta() {
        // Lógica para eliminar cuenta
    }
}
