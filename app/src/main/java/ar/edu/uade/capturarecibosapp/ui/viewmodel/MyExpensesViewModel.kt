package ar.edu.uade.capturarecibosapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.repository.ExpenseRepository
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseItem
import kotlinx.coroutines.launch

class MyExpensesViewModel : ViewModel() {
    private val expenseRepository = DependencyProvider.provideExpenseRepository()
    
    var totalSpent by mutableStateOf("$0,00")
        private set

    var statistics by mutableStateOf("Calculando...")
        private set

    var isLoading by mutableStateOf(false)

    private val _transactions = mutableStateListOf<ExpenseItem>()
    val transactions: List<ExpenseItem> get() = _transactions

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        isLoading = true
        viewModelScope.launch {
            val result = expenseRepository.getExpenses()
            isLoading = false
            if (result.isSuccess) {
                _transactions.clear()
                val dtos = result.getOrDefault(emptyList())
                // Mapear dtos a ExpenseItem de UI
                // dtos.forEach { dto -> _transactions.add(dto.toUiModel()) }
                totalSpent = "$${dtos.sumOf { it.amount }}"
            }
        }
    }
}
