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
                    photoUrl = 0, // Ticket usa String photoUrl, ExpenseItem usa Int. Por ahora 0.
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
            val apiFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val uiFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            java.time.LocalDate.parse(apiDate, apiFormatter).format(uiFormatter)
        } catch (e: Exception) {
            apiDate
        }
    }

    fun getIconForCategory(categoryName: String): String {
        return userCategories.value.find { it.name == categoryName }?.icon ?: "📁"
    }

    fun associateTicketToExpense(expense: ExpenseItem, photoRes: Int) {
        viewModelScope.launch {
            expenseRepository.saveExpense(expense.copy(photoUrl = photoRes))
        }
    }

    val totalSpent: StateFlow<String> = transactions.map { list ->
        val total = list.sumOf { it.amount }
        "$${String.format(Locale("es", "AR"), "%,.2f", total)}"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "$0,00"
    )

    var statistics by mutableStateOf("Vas por el 45% de tu presupuesto")
        private set

    init {
        // Podríamos disparar una sincronización aquí si fuera necesario
    }
}
