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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.data.model.TicketItem
import ar.edu.uade.capturarecibosapp.ui.components.TicketDetailDialog
import ar.edu.uade.capturarecibosapp.ui.viewmodel.WelcomeViewModel
import ar.edu.uade.capturarecibosapp.utils.getInitials

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onCategoriesClick: () -> Unit,
    onManualClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReportsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    val userName = viewModel.userName
    val totalSpent = viewModel.totalSpent
    val budgetPercentage = viewModel.budgetPercentage
    val recentTickets = viewModel.recentTickets
    val currentMonthLabel = viewModel.currentMonthLabel
    val monthComparisonText = viewModel.monthComparisonText
    val isSpendingDown = viewModel.isSpendingDown

    var selectedTicket by remember { mutableStateOf<TicketItem?>(null) }

    val initials = remember(userName) { getInitials(userName) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                TotalSpentCard(
                    totalSpent = totalSpent,
                    monthLabel = currentMonthLabel,
                    comparisonText = monthComparisonText,
                    isSpendingDown = isSpendingDown
                )
            }

            // PRESUPUESTO
            item {
                BudgetProgressCard(
                    budgetPercentage = budgetPercentage,
                    monthlyMax = viewModel.monthlyMaxLabel
                )
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
                Spacer(modifier = Modifier.height(16.dp))
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
                title = "Detalle del Gasto",
                commerce = ticket.commerce,
                date = ticket.date,
                amount = ticket.amount.toString(),
                category = ticket.category,
                description = ticket.description,
                photoUrl = ticket.photoUrl,
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
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClickLabel = "Ver perfil") { onProfileClick() },
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
private fun TotalSpentCard(
    totalSpent: String,
    monthLabel: String,
    comparisonText: String,
    isSpendingDown: Boolean
) {
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
                text = "Total gastado en $monthLabel",
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
                    imageVector = if (isSpendingDown)
                        Icons.AutoMirrored.Filled.TrendingDown
                    else
                        Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = if (isSpendingDown) "Tendencia de gasto hacia abajo" else "Tendencia de gasto hacia arriba",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " $comparisonText",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun BudgetProgressCard(budgetPercentage: Float, monthlyMax: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
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
                    Text("Límite: $monthlyMax", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (budgetPercentage > 1f) "Excedido" else "${(budgetPercentage * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = if (budgetPercentage > 1f) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { budgetPercentage.coerceAtMost(1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (budgetPercentage > 1f) Color.Red else MaterialTheme.colorScheme.primary,
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

        QuickActionItem(
            icon = Icons.Default.Add,
            label = "Manual",
            onClick = onManualClick)

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
        modifier = Modifier.clickable(onClickLabel = "Acción rápida $label") { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = "Icono de $label", tint = MaterialTheme.colorScheme.primary)
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
            .clickable(onClickLabel = "Ver detalle del ticket de $commerce") { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
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
                        contentDescription = "Icono de recibo",
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
