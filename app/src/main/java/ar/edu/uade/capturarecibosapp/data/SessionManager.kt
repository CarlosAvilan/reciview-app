package ar.edu.uade.capturarecibosapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "ReciViewSession"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_EMAIL = "userEmail"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            Log.d("SessionManager", "Initialized with context: ${context.packageName}")
        }
    }

    var userId: String?
        get() {
            val id = prefs?.getString(KEY_USER_ID, null)
            Log.d("SessionManager", "Reading userId: $id")
            return id
        }
        set(value) {
            Log.d("SessionManager", "Writing userId: $value")
            prefs?.edit()?.let {
                it.putString(KEY_USER_ID, value)
                it.apply()
            }
        }

    var userEmail: String?
        get() = prefs?.getString(KEY_USER_EMAIL, null)
        set(value) {
            prefs?.edit()?.let {
                it.putString(KEY_USER_EMAIL, value)
                it.apply()
            }
        }

    fun clear() {
        prefs?.edit()?.let {
            it.clear()
            it.apply()
        }
    }
}
