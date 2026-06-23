package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.DependencyProvider
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = DependencyProvider.provideUserRepository(application)
    private val ticketRepository = DependencyProvider.provideTicketRepository(application)
    private val expenseRepository = DependencyProvider.provideExpenseRepository(application)
    private val categoryRepository = DependencyProvider.provideCategoryRepository(application)

    private var monthlyMax: Float = 60000f

    val currentMonthLabel: String = LocalDate.now().format(
        DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("es-AR"))
    )

    var userName by mutableStateOf("")
        private set

    var totalSpent by mutableStateOf("$0")
        private set

    var totalSpentValue by mutableFloatStateOf(0f)
        private set

    var budgetPercentage by mutableFloatStateOf(0f)
        private set

    var monthlyMaxLabel by mutableStateOf("$60.000")
        private set

    var monthComparisonText by mutableStateOf("Calculando...")
        private set

    var isSpendingDown by mutableStateOf(true)
        private set

    var recentTickets by mutableStateOf<List<TicketItem>>(emptyList())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        val userId = SessionManager.userId ?: return
        
        viewModelScope.launch {
            // Sincronizar preferencias de Supabase
            userRepository.fetchAndCachePreferences()

            launch {
                userRepository.getProfile()
                    .onSuccess { profile ->
                        userName = resolveDisplayName(profile.name, profile.email)
                    }
                    .onFailure {
                        userName = resolveDisplayName(null, SessionManager.userEmail)
                    }
            }

            // Observar preferencias (presupuesto)
            launch {
                userRepository.getLocalPreferences().collectLatest { prefs ->
                    prefs?.let {
                        monthlyMax = it.monthlyMax
                        monthlyMaxLabel = "$${formatMonto(monthlyMax.toDouble())}"
                        updateBudgetPercentage()
                    }
                }
            }

            // Observar gastos y tickets para calcular el total
            launch {
                combine(
                    ticketRepository.getTickets(userId),
                    expenseRepository.getExpensesForUser(userId),
                    categoryRepository.getCategories(userId)
                ) { tickets, expenses, categories ->
                    Triple(tickets, expenses, categories)
                }.collectLatest { (tickets, expenses, categories) ->
                    val now = LocalDate.now()
                    val currentMonthTickets = tickets.filter {
                        try {
                            val d = LocalDate.parse(it.createdAt.substringBefore('T'))
                            d.year == now.year && d.monthValue == now.monthValue
                        } catch (e: Exception) { false }
                    }
                    val currentMonthExpenses = expenses.filter {
                        try {
                            val d = LocalDate.parse(it.date.substringBefore('T'))
                            d.year == now.year && d.monthValue == now.monthValue
                        } catch (e: Exception) { false }
                    }

                    val total = currentMonthTickets.sumOf { it.amount.toDouble() } + 
                                currentMonthExpenses.sumOf { it.amount }
                    
                    totalSpentValue = total.toFloat()
                    totalSpent = "$${formatMonto(total)}"
                    updateBudgetPercentage()

                    // Actividad reciente
                    recentTickets = tickets.take(5).map { ticket ->
                        val catName = categories.find { it.id == ticket.categoryId }?.name ?: "Otros"
                        TicketItem(
                            id = ticket.id,
                            ticketId = ticket.id,
                            commerce = ticket.establishment,
                            date = ticket.createdAt,
                            amount = ticket.amount,
                            category = catName,
                            description = ticket.description
                        )
                    }
                    
                    // Comparación básica (mock o implementar lógica de mes anterior)
                    monthComparisonText = "0% más que el mes pasado"
                    isSpendingDown = true
                }
            }
        }
    }

    private fun resolveDisplayName(name: String?, email: String?): String {
        if (name != null) return name;
        
        val emailName = email?.substringBefore("@")?.trim()?.takeIf { it.isNotBlank() }
        if (emailName != null) return emailName

        return "Usuario"
    }

    private fun updateBudgetPercentage() {
        budgetPercentage = (totalSpentValue / monthlyMax).coerceIn(0f, 1f)
    }

    private fun formatMonto(amount: Double): String =
        String.format(Locale.US, "%,.0f", amount).replace(",", ".")
}
