package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MyExpensesViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val userRepository = DependencyProvider.provideUserRepository(application)
    private val userId = SessionManager.userId ?: ""
    
    // Cache de categorías para evitar múltiples suscripciones
    val userCategories: StateFlow<List<UserCategory>> = expenseRepository.categoryDao.getCategoriesForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val ticketsWithCategories: Flow<List<ExpenseItem>> = ticketRepository.getTicketsWithCategories(userId)
        .map { list ->
            list.map { (ticket, category) ->
                ExpenseItem(
                    id = ticket.id,
                    photoUrl = ticket.photoUrl,
                    userId = ticket.userId,
                    title = ticket.establishment,
                    date = formatTicketDate(ticket.createdAt),
                    category = category?.name ?: "Sin categoría",
                    amount = ticket.amount.toDouble()
                )
            }
        }

    val transactions: StateFlow<List<ExpenseItem>> = ticketsWithCategories
        .map { it.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTransactions: StateFlow<List<ExpenseItem>> = ticketsWithCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun formatTicketDate(apiDate: String): String {
        return try {
            val cleanDate = apiDate.substringBefore('T')
            val apiFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val uiFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            java.time.LocalDate.parse(cleanDate, apiFormatter).format(uiFormatter)
        } catch (e: Exception) {
            apiDate
        }
    }

    fun getIconForCategory(categoryName: String): String? {
        if (categoryName == "Sin categoría") return null
        return userCategories.value.find { it.name == categoryName }?.icon ?: "📁"
    }

    // Nota: associateTicketToExpense ya no es necesario o debería usar photoUrl String
    fun associateTicketToExpense(expense: ExpenseItem, photoPath: String) {
        viewModelScope.launch {
            expenseRepository.saveExpense(expense.copy(photoUrl = photoPath))
        }
    }

    val totalSpent: StateFlow<String> = allTransactions.map { list ->
        val now = java.time.LocalDate.now()
        val uiFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val total = list.filter { expense ->
            try {
                val d = java.time.LocalDate.parse(expense.date, uiFormatter)
                d.year == now.year && d.monthValue == now.monthValue
            } catch (e: Exception) { false }
        }.sumOf { it.amount }
        "$${String.format(Locale("es", "AR"), "%,.2f", total)}"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "$0,00"
    )

    var statistics by mutableStateOf("Calculando...")
        private set

    init {
        viewModelScope.launch {
            val monthlyMaxFlow = userRepository.getLocalPreferences()
                .map { it?.monthlyMax ?: 60000f }

            combine(allTransactions, monthlyMaxFlow) { list, monthlyMax ->
                val now = java.time.LocalDate.now()
                val uiFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val total = list.filter { expense ->
                    try {
                        val d = java.time.LocalDate.parse(expense.date, uiFormatter)
                        d.year == now.year && d.monthValue == now.monthValue
                    } catch (e: Exception) { false }
                }.sumOf { it.amount }

                if (monthlyMax <= 0f) "Sin presupuesto configurado"
                else {
                    val pct = ((total / monthlyMax) * 100).toInt().coerceIn(0, 100)
                    "Vas por el $pct% de tu presupuesto"
                }
            }.collect { text -> statistics = text }
        }
    }
}
