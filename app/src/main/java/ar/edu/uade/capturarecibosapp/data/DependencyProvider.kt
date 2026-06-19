package ar.edu.uade.capturarecibosapp.data

import ar.edu.uade.capturarecibosapp.data.remote.*
import ar.edu.uade.capturarecibosapp.data.repository.*

/**
 * DependencyProvider: Contenedor central para la Inyección de Dependencias manual.
 * 
 * Aquí es donde "viven" los ApiServices. Si en el futuro usas Hilt o Koin,
 * este archivo será reemplazado por Módulos (@Module).
 */
object DependencyProvider {

    private val authApiService by lazy { 
        RetrofitClient.createService(AuthApiService::class.java) 
    }

    private val userApiService by lazy { 
        RetrofitClient.createService(UserApiService::class.java) 
    }

    private val expenseApiService by lazy { 
        RetrofitClient.createService(ExpenseApiService::class.java) 
    }

    private val ticketApiService by lazy { 
        RetrofitClient.createService(TicketApiService::class.java) 
    }

    private val categoryApiService by lazy {
        RetrofitClient.createService(CategoryApiService::class.java)
    }


    // --- Repositorios (Lógica de Datos) ---
    // Cada repositorio recibe su ApiService por constructor (Inversión de Dependencias).

    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(authApiService)
    }

    fun provideUserRepository(context: android.content.Context): UserRepository {
        val database = ar.edu.uade.capturarecibosapp.data.local.AppDatabase.getDatabase(context)
        return UserRepository(userApiService, database.userDao())
    }

    fun provideExpenseRepository(context: android.content.Context): ExpenseRepository {
        val database = ar.edu.uade.capturarecibosapp.data.local.AppDatabase.getDatabase(context)
        return ExpenseRepository(database.expenseDao(), database.categoryDao(), database.ticketDao(), expenseApiService)
    }

    fun provideTicketRepository(context: android.content.Context): TicketRepository {
        val database = ar.edu.uade.capturarecibosapp.data.local.AppDatabase.getDatabase(context)
        return TicketRepository(database.ticketDao(), database.categoryDao(), ticketApiService)
    }

    fun provideCategoryRepository(context: android.content.Context): CategoryRepository {
        val database = ar.edu.uade.capturarecibosapp.data.local.AppDatabase.getDatabase(context)
        return CategoryRepository(database.categoryDao(), categoryApiService)
    }
}
