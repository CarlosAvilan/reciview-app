package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class FaqItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

data class TipItem(
    val title: String,
    val description: String
)

class HelpViewModel : ViewModel() {
    val tips = listOf(
        TipItem("Usar buena iluminación", "Evita las sombras sobre el ticket."),
        TipItem("Papel bien estirado", "Aplaná el ticket antes de la foto.")
    )

    var faqs = mutableStateListOf(
        FaqItem(
            "¿Cómo exporto mis gastos?",
            "Podés exportar tus reportes mensuales entrando a la sección de Reportes. Al final de la pantalla seleccioná el formato (.PDF o .Excel) y hacé clic en Descargar. También podés enviarlos directamente por mail o compartirlos por WhatsApp."
        ),
        FaqItem(
            "¿Mis datos están seguros?",
            "Sí, tus datos están encriptados y protegidos según las normas de seguridad vigentes."
        ),
        FaqItem(
            "¿Qué pasa si el ticket está borroso?",
            "El sistema podría no reconocerlo correctamente. Te recomendamos volver a sacarle una foto con mejor luz."
        ),
        FaqItem(
            "¿Puedo usar tickets de cualquier país?",
            "Actualmente soportamos tickets de Argentina, pero estamos trabajando para expandirnos."
        )
    )

    fun toggleFaq(index: Int) {
        faqs[index] = faqs[index].copy(isExpanded = !faqs[index].isExpanded)
    }
}
