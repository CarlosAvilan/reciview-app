package ar.edu.uade.capturarecibosapp.data.local.seeders

import ar.edu.uade.capturarecibosapp.data.model.AdviceItem
import ar.edu.uade.capturarecibosapp.data.model.FaqItem

class HelpSeeder {
    fun provideInitialAdvice(): List<AdviceItem> = listOf(
        AdviceItem(title = "Usar buena iluminación", description = "Evita las sombras sobre el ticket.", icon = "💡"),
        AdviceItem(title = "Papel bien estirado", description = "Aplaná el ticket antes de la foto.", icon = "📄")
    )

    fun provideInitialFaqs(): List<FaqItem> = listOf(
        FaqItem(question = "¿Cómo exporto mis gastos?", answer = "Podés exportar tus reportes mensuales entrando a la sección de Reportes. Al final de la pantalla seleccioná el formato (.PDF) y hacé clic en Descargar."),
        FaqItem(question = "¿Mis datos están seguros?", answer = "Sí, tus datos están encriptados y protegidos según las normas de seguridad vigentes."),
        FaqItem(question = "¿Qué pasa si el ticket está borroso?", answer = "El sistema podría no reconocerlo correctamente. Te recomendamos volver a sacarle una foto con mejor luz o intentar una carga manual."),
        FaqItem(question = "¿Puedo usar tickets de cualquier país?", answer = "Actualmente soportamos tickets de Argentina, pero estamos trabajando para expandirnos.")
    )
}