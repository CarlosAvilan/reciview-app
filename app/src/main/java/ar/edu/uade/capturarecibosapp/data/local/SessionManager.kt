package ar.edu.uade.capturarecibosapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // Creamos el archivo de preferencias llamado "user_session"
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
    }

    // Guarda el ID cuando el usuario se loguea o registra
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    // Recupera el ID (si devuelve null, significa que no está logueado)
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Para cuando el usuario decida cerrar sesión
    fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

}
