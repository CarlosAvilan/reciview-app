package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.ui.components.TicketCard
import ar.edu.uade.capturarecibosapp.ui.components.TicketDetailDialog
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.TicketsViewModel
import java.util.Locale

@Composable
fun TicketsScreen(
    viewModel: TicketsViewModel = viewModel(),
) {
    // Accedemos directamente a las propiedades del ViewModel (mutableStateOf)
    val filteredTickets = viewModel.filteredTickets
    val categoryNames = viewModel.categoryNames
    val categoryList = viewModel.categoryList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Archivo de Tickets",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    "Buscar por comercio...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chips de Categorías dinámicos
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 24.dp)
        ) {
            items(categoryNames) { category ->
                val isSelected = viewModel.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline,
                        enabled = true,
                        selected = isSelected,
                        borderWidth = 1.dp
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de Tickets
        if (filteredTickets.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("No se encontraron tickets", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTickets) { ticket ->
                    TicketCard(
                        commerce = ticket.establishment,
                        date = ticket.createdAt,
                        amount = String.format(Locale.getDefault(), "$ %.2f", ticket.amount),
                        imageRes = null, 
                        onClick = { viewModel.selectedTicket = ticket }
                    )
                }
            }
        }
    }

    // Diálogo de Detalle
    viewModel.selectedTicket?.let { ticket ->
        // Obtenemos el nombre real de la categoría comparando el id (Long) con categoryId (Long?)
        val categoryName = categoryList.find { it.id == ticket.categoryId }?.name ?: "Sin categoría"
        
        TicketDetailDialog(
            commerce = ticket.establishment,
            date = ticket.createdAt,
            amount = String.format(Locale.getDefault(), "$ %.2f", ticket.amount),
            category = categoryName,
            description = ticket.description,
            imageRes = null,
            onDismiss = { viewModel.selectedTicket = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    ReciViewTheme {
        TicketsScreen()
    }
}
