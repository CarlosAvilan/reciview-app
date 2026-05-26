package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var nombreCompleto by mutableStateOf("")
    var correoElectronico by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var paisNacimiento by mutableStateOf("")
    var password by mutableStateOf("")

    var terminosAceptados by mutableStateOf(false)
    var permisosCamaraAceptados by mutableStateOf(false)
    var haLeidoTerminos by mutableStateOf(false)

    fun onNombreChange(newValue: String) {
        nombreCompleto = newValue
    }

    fun onCorreoChange(newValue: String) {
        correoElectronico = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun onFechaNacimientoChange(newValue: String) {
        fechaNacimiento = newValue
    }

    fun onPaisChange(newValue: String) {
        paisNacimiento = newValue
    }

    fun onTerminosAceptadosChange(newValue: Boolean) {
        terminosAceptados = newValue
    }

    fun onPermisosCamaraAceptadosChange(newValue: Boolean) {
        permisosCamaraAceptados = newValue
    }

    fun marcarTerminosComoLeidos() {
        haLeidoTerminos = true
    }

    fun registrarse(onSuccess: () -> Unit) {
        onSuccess()
    }
}
