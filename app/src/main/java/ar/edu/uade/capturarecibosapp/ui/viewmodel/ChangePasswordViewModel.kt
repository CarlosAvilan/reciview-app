package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ChangePasswordViewModel : ViewModel (){
    var contraseniaAnterior by mutableStateOf("")
    var nuevaContrasenia by mutableStateOf("")
    var nuevaContraseniaRepetida by mutableStateOf("")

    fun onContraseniaAnteriorChange(newValue: String) {
        contraseniaAnterior = newValue
    }

    fun onNuevaContraseniaChange(newValue: String) {
        nuevaContrasenia = newValue
    }

    fun onNuevaContraseniaRepetidaChange(newValue: String) {
        nuevaContraseniaRepetida = newValue
    }

    fun cambiarContrasenia() {
        // TODO
    }
}
