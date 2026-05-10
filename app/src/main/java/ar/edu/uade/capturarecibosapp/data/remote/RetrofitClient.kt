package ar.edu.uade.capturarecibosapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Si usas el emulador, usa "10.0.2.2" en lugar de "localhost"
    // Si usas celular físico, usa la IP de tu PC (ej: "192.168.1.50")
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}