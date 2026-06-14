package ar.edu.uade.capturarecibosapp.data.remote

import ar.edu.uade.capturarecibosapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient: Fábrica genérica de servicios de red.
 *
 * Su única responsabilidad es configurar la infraestructura de red.
 */
object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Crea una implementación de la interfaz de servicio definida por [serviceClass].
     * Este método permite que los repositorios obtengan sus dependencias de red
     * sin que esta clase necesite conocer cada servicio individualmente.
     *
     * Ejemplo: val service = RetrofitClient.createService(ExpenseApiService::class.java)
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
