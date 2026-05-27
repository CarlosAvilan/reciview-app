package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class TutorialPageData(
    val title: String,
    val description: String,
    val imageRes: Int
)

class TutorialViewModel : ViewModel() {

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    val pages = listOf(
        TutorialPageData(
            title = "Captura Instantánea",
            description = "Digitalizá tus tickets físicos en segundos usando la cámara de tu celular.",
            imageRes = R.drawable.tutorial1
        ),
        TutorialPageData(
            title = "Extracción Inteligente",
            description = "Nuestro sistema procesa el texto automáticamente para identificar el comercio y el monto total sin que tengas que tipear de más.",
            imageRes = R.drawable.tutorial2
        ),
        TutorialPageData(
            title = "Control de Gastos",
            description = "Visualizá tus consumos mediante gráficos y mantené tus finanzas al día, incluso si te quedás sin conexión a internet.",
            imageRes = R.drawable.tutorial3
        )
    )

    fun onTutorialFinished() {
        viewModelScope.launch {
            _navigateToLogin.emit(Unit)
        }
    }
}
