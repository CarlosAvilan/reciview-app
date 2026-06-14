package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.BuildConfig
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.remote.dto.AuthResponseDTO
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Gestiona la autenticación por token en la app y el refresco automático del mismo en caso de vencimiento
 */
class TokenAuthenticator : Authenticator {

    private interface AuthRefreshService {
        @POST("auth/v1/token?grant_type=refresh_token")
        fun refreshTokenSync(@Body body: Map<String, String>): retrofit2.Call<AuthResponseDTO>
    }

    private val authServiceSync: AuthRefreshService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("apikey", BuildConfig.API_KEY)
                        .build()
                    chain.proceed(request)
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthRefreshService::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar loop si el error 401 viene del propio refresh
        if (response.request.url.encodedPath.contains("auth/v1/token")) {
            return null
        }

        synchronized(this) {
            val refreshToken = SessionManager.refreshToken ?: return null
            
            val refreshResponse = authServiceSync.refreshTokenSync(
                mapOf("refresh_token" to refreshToken)
            ).execute()

            return if (refreshResponse.isSuccessful) {
                val newAuthData = refreshResponse.body()
                if (newAuthData != null) {
                    SessionManager.accessToken = newAuthData.accessToken
                    SessionManager.refreshToken = newAuthData.refreshToken
                    
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newAuthData.accessToken}")
                        .build()
                } else null
            } else {
                SessionManager.clear()
                null
            }
        }
    }
}
