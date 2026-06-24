package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseCard
import ar.edu.uade.capturarecibosapp.ui.components.TicketDetailDialog
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.MyExpensesViewModel

@Composable
fun MyExpensesScreen(
    viewModel: MyExpensesViewModel = viewModel(),
    onCategoriesClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onScanClick: () -> Unit = {}
) {
    val totalGastado by viewModel.totalSpent.collectAsState()
    val estadistica = viewModel.statistics
    val isOverBudget = viewModel.isOverBudget
    val transacciones by viewModel.transactions.collectAsState()
    val userCategories by viewModel.userCategories.collectAsState() // Suscribirse para reaccionar a cambios

    var selectedTransaccion by remember { mutableStateOf<ExpenseItem?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mis Gastos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Tarjeta de Gasto Total
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Gasto Total del Mes",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = totalGastado,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = estadistica,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        shape = CircleShape,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isOverBudget)
                                Color.Red.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ),
                        border = null
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gastos Recientes",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ver todo",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClickLabel = "Ver todas las transacciones") { onViewAllClick() }
                )
            }
        }

        items(transacciones) { transaccion ->
            ExpenseCard(
                title = transaccion.title,
                date = transaccion.date,
                category = transaccion.category,
                categoryIcon = viewModel.getIconForCategory(transaccion.category),
                amount = transaccion.amount,
                photoUrl = transaccion.photoUrl,
                onAddTicketClick = {
                    onScanClick()
                },
                onCardClick = { selectedTransaccion = transaccion }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClickLabel = "Ver gastos por categoría") { onCategoriesClick() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gastos por categoría",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ir a gastos por categoría",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    selectedTransaccion?.let { transaccion ->
        TicketDetailDialog(
            title = "Detalle del Gasto",
            commerce = transaccion.title,
            date = transaccion.date,
            amount = transaccion.amount.toString(),
            category = transaccion.category,
            description = "",
            photoUrl = transaccion.photoUrl,
            onDismiss = { selectedTransaccion = null }
        )
    }
    } // Box
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyExpensesScreenPreview() {
    ReciViewTheme {
        MyExpensesScreen(
            onCategoriesClick = {},
            onViewAllClick = {}
        )
    }
}
