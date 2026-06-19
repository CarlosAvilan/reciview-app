package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.BuildConfig
import ar.edu.uade.capturarecibosapp.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Inyecta los headers necesarios para peticiones a la API
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            // La apikey se inyecta en todas las peticiones
            .header("apikey", BuildConfig.API_KEY)
            .header("Content-Type", "application/json")

        // Solo agregar Prefer si no está ya definido en la interfaz del servicio
        if (original.header("Prefer") == null) {
            requestBuilder.header("Prefer", "return=representation")
        }

        val path = original.url.encodedPath
        // En endpoints de autenticación, el header Authorization debe ser nulo
        val isAuthEndpoint = path.contains("auth/v1/")

        if (!isAuthEndpoint) {
            val token = SessionManager.accessToken ?: BuildConfig.API_KEY
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
