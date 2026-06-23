package ar.edu.uade.capturarecibosapp.domain

object InputValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isValidPhone(phone: String): Boolean = phone.isBlank() || phone.trim().length >= 7
}
