package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseCard
import ar.edu.uade.capturarecibosapp.ui.viewmodel.MyExpensesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    viewModel: MyExpensesViewModel,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit = {}
) {
    val transacciones by viewModel.allTransactions.collectAsState()
    val userCategories by viewModel.userCategories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(transacciones) { transaccion ->
                ExpenseCard(
                    title = transaccion.title,
                    date = transaccion.date,
                    category = transaccion.category,
                    categoryIcon = viewModel.getIconForCategory(transaccion.category),
                    amount = transaccion.amount,
                    imageRes = transaccion.photoUrl,
                    onAddTicketClick = {
                        onScanClick()
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
