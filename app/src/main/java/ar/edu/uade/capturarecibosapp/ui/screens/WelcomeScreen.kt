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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.ui.components.TicketDetailDialog
import ar.edu.uade.capturarecibosapp.ui.viewmodel.TicketItem
import ar.edu.uade.capturarecibosapp.ui.viewmodel.WelcomeViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(),
    onCategoriesClick: () -> Unit,
    onManualClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReportsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onScanClick: () -> Unit
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
            .background(Color(0xFFF8F9FA))
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
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(recentTickets) { ticket ->
                RecentActivityItem(
                    ticket = ticket,
                    onClick = { selectedTicket = ticket }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Diálogo de detalle
        selectedTicket?.let { ticket ->
            TicketDetailDialog(
                ticket = ticket,
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
                color = Color.Gray
            )
            Text(
                text = "Bienvenido a ReciView",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E7FF))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F8CF6)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4F8CF6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total gastado en mayo",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = totalSpent,
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " 5% menos que el mes pasado",
                    color = Color.White,
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
            style = MaterialTheme.typography.titleMedium
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Límite: $60.000", color = Color.Gray, fontSize = 14.sp)
                    Text("${(budgetPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { budgetPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFF4F8CF6),
                    trackColor = Color(0xFFE9ECEF)
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
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF4F8CF6))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun RecentActivityItem(
    ticket: TicketItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .background(Color(0xFFF1F3F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = Color(0xFF4F8CF6),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = ticket.commerce, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${ticket.date} • ${ticket.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            Text(text = ticket.amount, fontWeight = FontWeight.Bold)
        }
    }
}
