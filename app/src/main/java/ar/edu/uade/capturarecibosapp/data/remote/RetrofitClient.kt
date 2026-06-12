package ar.edu.uade.capturarecibosapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // La BASE_URL debe ser la raíz para permitir el acceso a /auth y /rest
    private const val BASE_URL = "https://rzebtxdswubvzbgwkvab.supabase.co/"
    
    // PEGA AQUÍ TU SUPABASE_KEY REAL
    private const val SUPABASE_KEY = "aca poner la clave, tuve que borrar por temas de seg de github"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .addHeader("Content-Type", "application/json")
                // Prefer representation es clave para recibir el objeto insertado en Supabase
                .addHeader("Prefer", "return=representation")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val expenseService: ExpenseApiService by lazy {
        retrofit.create(ExpenseApiService::class.java)
    }

    val ticketService: TicketApiService by lazy {
        retrofit.create(TicketApiService::class.java)
    }

    val userService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}
