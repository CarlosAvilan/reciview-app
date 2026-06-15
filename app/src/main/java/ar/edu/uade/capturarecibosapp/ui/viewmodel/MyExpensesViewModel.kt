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
    private val userId = SessionManager.userId ?: ""
    
    // Cache de categorías para evitar múltiples suscripciones
    val userCategories: StateFlow<List<UserCategory>> = expenseRepository.categoryDao.getCategoriesForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactions: StateFlow<List<ExpenseItem>> = expenseRepository.getExpensesForUser(userId)
        .map { it.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTransactions: StateFlow<List<ExpenseItem>> = expenseRepository.getExpensesForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
