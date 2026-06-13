package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.ui.components.TicketDetailDialog
import ar.edu.uade.capturarecibosapp.ui.viewmodel.WelcomeViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(),
    onCategoriesClick: () -> Unit,
    onManualClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReportsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    // Obtenemos el estado desde el ViewModel
    val userName = viewModel.userName
    val totalSpent = viewModel.totalSpent
    val budgetPercentage = viewModel.budgetPercentage
    val recentTickets = viewModel.recentTickets

    var selectedTicket by remember { mutableStateOf<TicketItem?>(null) }

    val initials = userName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // HEADER
            item {
                WelcomeHeader(
                    userName = userName,
                    initials = initials,
                    onProfileClick = onProfileClick
                )
            }

            // SUMMARY CARD
            item {
                TotalSpentCard(totalSpent = totalSpent)
            }

            // PRESUPUESTO
            item {
                BudgetProgressCard(budgetPercentage = budgetPercentage)
            }

            // ACCIONES RÁPIDAS
            item {
                QuickActionsRow(
                    onManualClick = onManualClick,
                    onCategoriesClick = onCategoriesClick,
                    onReportsClick = onReportsClick,
                    onHelpClick = onHelpClick
                )
            }

            // ACTIVIDAD RECIENTE
            item {
                Text(
                    text = "Actividad Reciente",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(recentTickets) { ticket ->
                // Siguiendo SOLID: Pasamos parámetros puros en lugar del objeto TicketItem
                RecentActivityItem(
                    commerce = ticket.commerce,
                    date = ticket.date,
                    category = ticket.category,
                    amount = ticket.amount.toString(),
                    onClick = { selectedTicket = ticket }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // Diálogo de detalle - Actualizado para usar parámetros puros
        selectedTicket?.let { ticket ->
            TicketDetailDialog(
                commerce = ticket.commerce,
                date = ticket.date,
                amount = ticket.amount.toString(),
                category = ticket.category,
                description = ticket.description,
                imageRes = ticket.imageRes,
                onDismiss = { selectedTicket = null }
            )
        }
    }
}

@Composable
private fun WelcomeHeader(
    userName: String,
    initials: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hola, $userName 👋",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Bienvenido a ReciView",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun TotalSpentCard(totalSpent: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
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
                text = "Total gastado en mayo",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = totalSpent,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " 5% menos que el mes pasado",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun BudgetProgressCard(budgetPercentage: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Tu Presupuesto Mensual",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Límite: $60.000", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text("${(budgetPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { budgetPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onManualClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onReportsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(icon = Icons.Default.Add, label = "Manual", onClick = onManualClick)
        QuickActionItem(
            icon = Icons.Default.Category,
            label = "Categorías",
            onClick = onCategoriesClick
        )
        QuickActionItem(
            icon = Icons.Default.BarChart,
            label = "Reportes",
            onClick = onReportsClick
        )
        QuickActionItem(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            label = "Ayuda",
            onClick = onHelpClick
        )
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun RecentActivityItem(
    commerce: String,
    date: String,
    category: String,
    amount: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = commerce, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "$date • $category",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(text = amount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
