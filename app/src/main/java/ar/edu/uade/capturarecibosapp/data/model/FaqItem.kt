package ar.edu.uade.capturarecibosapp.data.model

data class FaqItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
