package ar.edu.uade.capturarecibosapp.utils

/**
 * Obtiene las iniciales de un nombre de usuario.
 * Si el nombre tiene dos o más palabras, toma la primera letra de las dos primeras.
 * Si tiene una sola palabra, toma la primera letra.
 * Si el nombre es nulo, vacío o indica un estado de carga/error, devuelve "?".
 */
fun getInitials(name: String?): String {
    if (name.isNullOrBlank()) {
        return "?"
    }

    val words = name.trim().split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .take(2)

    return if (words.isEmpty()) {
        "?"
    } else {
        words.joinToString("") { it.take(1).uppercase() }
    }
}
