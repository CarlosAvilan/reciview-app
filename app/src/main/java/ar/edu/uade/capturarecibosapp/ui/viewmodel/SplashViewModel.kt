package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToNext = MutableSharedFlow<Unit>()
    val navigateToNext = _navigateToNext.asSharedFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            // Simulamos la carga inicial (ej: chequear sesión, cargar caché, etc)
            delay(2000)
            _navigateToNext.emit(Unit)
        }
    }
}
