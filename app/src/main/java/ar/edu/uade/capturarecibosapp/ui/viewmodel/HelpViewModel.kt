package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.model.FaqItem
import ar.edu.uade.capturarecibosapp.data.model.AdviceItem


class HelpViewModel : ViewModel() {
    val tips = listOf(
        AdviceItem(
            title = "Usar buena iluminación",
            description = "Evita las sombras sobre el ticket.",
            iconName = "Lightbulb"
        ),
        AdviceItem(
            title = "Papel bien estirado",
            description = "Aplaná el ticket antes de la foto.",
            iconName = "ReceiptLong"
        )
    )

    //Icons.Default.ReceiptLong

    var faqs = mutableStateListOf(
        FaqItem(
            question = "¿Cómo exporto mis gastos?",
            answer = "Podés exportar tus reportes mensuales entrando a la sección de Reportes. Al final de la pantalla seleccioná el formato (.PDF) y hacé clic en Descargar."
        ),
        FaqItem(
            question = "¿Mis datos están seguros?",
            answer = "Sí, tus datos están encriptados y protegidos según las normas de seguridad vigentes."
        ),
        FaqItem(
            question = "¿Qué pasa si el ticket está borroso?",
            answer = "El sistema podría no reconocerlo correctamente.Te recomendamos volver a sacarle una foto con mejor luz o intentar una carga manual"
        ),
        FaqItem(
            question = "¿Puedo usar tickets de cualquier país?",
            answer = "Actualmente soportamos tickets de Argentina, pero estamos trabajando para expandirnos."
        )
    )

    fun toggleFaq(index: Int) {
        faqs[index] = faqs[index].copy(isExpanded = !faqs[index].isExpanded)
    }
}
