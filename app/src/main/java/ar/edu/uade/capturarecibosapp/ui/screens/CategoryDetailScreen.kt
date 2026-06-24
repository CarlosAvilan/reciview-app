package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import ar.edu.uade.capturarecibosapp.ui.components.*
import ar.edu.uade.capturarecibosapp.ui.viewmodel.CategoryDetailUiState
import ar.edu.uade.capturarecibosapp.ui.viewmodel.CategoryDetailViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: String,
    viewModel: CategoryDetailViewModel,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Detalle de Categoría",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Categoría",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (val state = uiState) {
            is CategoryDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CategoryDetailUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CategoryDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // --- FORMULARIO DE EDICIÓN ---
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            SectionLabel(text = "NOMBRE")
                            TextField(
                                value = viewModel.editName,
                                onValueChange = { viewModel.editName = it },
                                placeholder = "Nombre de la categoría",
                                isError = viewModel.nameError
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            SectionLabel(text = "LÍMITE MENSUAL")
                            TextField(
                                value = viewModel.editBudget,
                                onValueChange = { viewModel.editBudget = it },
                                placeholder = "$0.00",
                                isError = viewModel.budgetError
                            )
                        }
                    }

                    item {
                        if (viewModel.errorMessage != null) {
                            Text(
                                text = viewModel.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 28.dp)
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            SectionLabel(text = "ICONO")
                            val emojis = listOf("🛒", "🚗", "🍔", "💡", "🎮", "🏥", "👔", "🏠", "✈️", "🎓")
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(emojis) { emoji ->
                                    val isSelected = viewModel.editIcon == emoji
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.editIcon = emoji },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Button(
                                text = "Guardar cambios",
                                onClick = { viewModel.saveChanges() }
                            )
                        }
                    }

                    // --- LISTA DE GASTOS ---
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gastos Asociados",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Filtrar por fecha")
                                }
                            }
                        }
                        
                        if (viewModel.dateFilterStart != null || viewModel.dateFilterEnd != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .padding(horizontal = 8.dp), // Reducido aún más (de 12.dp a 8.dp) para alineación "natural"
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DateFilterBox(
                                    label = "Desde",
                                    date = viewModel.dateFilterStart?.toString() ?: "...",
                                    modifier = Modifier.weight(1f),
                                    onClick = { showDatePicker = true }
                                )
                                DateFilterBox(
                                    label = "Hasta",
                                    date = viewModel.dateFilterEnd?.toString() ?: "...",
                                    modifier = Modifier.weight(1f),
                                    onClick = { showDatePicker = true }
                                )
                            }
                            Text(
                                text = "Limpiar filtro",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { viewModel.updateDateFilter(null, null) }
                                    .padding(top = 4.dp, start = 24.dp)
                            )
                        }
                    }

                    if (state.expenses.isEmpty()) {
                        item {
                            Text(
                                text = "No hay gastos en esta categoría",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp).padding(horizontal = 24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(state.expenses) { expense ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                ExpenseCard(
                                    title = expense.title,
                                    date = expense.date,
                                    category = expense.category,
                                    categoryIcon = state.category.icon,
                                    amount = expense.amount,
                                    photoUrl = expense.photoUrl,
                                    onAddTicketClick = {
                                        onScanClick()
                                    }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    val end = dateRangePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    viewModel.updateDateFilter(start, end)
                    showDatePicker = false
                }) {
                    Text("Filtrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
                title = {
                    Text(
                        text = "Seleccionar fecha",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp)
                    )
                },
                headline = {
                    val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Desde",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = startDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Inicio",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = " — ",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hasta",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = endDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Fin",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            )
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar Categoría") },
            text = { Text("¿Estás seguro de que deseas eliminar esta categoría? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DateFilterBox(
    label: String,
    date: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp), // Reducido horizontal de 16.dp a 8.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
