package ar.edu.uade.capturarecibosapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "ReciViewSession"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_REFRESH_TOKEN = "refreshToken"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            Log.d("SessionManager", "Initialized with context: ${context.packageName}")
        }
    }

    var userId: String?
        get() = prefs?.getString(KEY_USER_ID, null)
        set(value) {
            prefs?.edit()?.putString(KEY_USER_ID, value)?.apply()
        }

    var userEmail: String?
        get() = prefs?.getString(KEY_USER_EMAIL, null)
        set(value) {
            prefs?.edit()?.putString(KEY_USER_EMAIL, value)?.apply()
        }

    var accessToken: String?
        get() = prefs?.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_ACCESS_TOKEN, value)?.apply()
        }

    var refreshToken: String?
        get() = prefs?.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_REFRESH_TOKEN, value)?.apply()
        }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
