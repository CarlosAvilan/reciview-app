package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import ar.edu.uade.capturarecibosapp.data.model.FaqItem
import ar.edu.uade.capturarecibosapp.data.model.TipItem


class HelpViewModel : ViewModel() {
    val tips = listOf(
        TipItem(
            "Usar buena iluminación", 
            "Evita las sombras sobre el ticket.",
            Icons.Default.Lightbulb
        ),
        TipItem(
            "Papel bien estirado", 
            "Aplaná el ticket antes de la foto.",
            Icons.Default.ReceiptLong
        )
    )

    var faqs = mutableStateListOf(
        FaqItem(
            "¿Cómo exporto mis gastos?",
            "Podés exportar tus reportes mensuales entrando a la sección de Reportes. Al final de la pantalla seleccioná el formato (.PDF) y hacé clic en Descargar."
        ),
        FaqItem(
            "¿Mis datos están seguros?",
            "Sí, tus datos están encriptados y protegidos según las normas de seguridad vigentes."
        ),
        FaqItem(
            "¿Qué pasa si el ticket está borroso?",
            "El sistema podría no reconocerlo correctamente.Te recomendamos volver a sacarle una foto con mejor luz o intentar una carga manual"
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
