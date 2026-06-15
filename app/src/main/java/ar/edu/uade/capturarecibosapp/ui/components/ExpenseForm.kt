package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseForm(
    monto: String,
    onMontoChange: (String) -> Unit,
    montoError: Boolean,
    establecimiento: String,
    onEstablecimientoChange: (String) -> Unit,
    establecimientoError: Boolean,
    categoria: String,
    onCategoriaChange: (String) -> Unit,
    categoriaError: Boolean,
    categoriesList: List<UserCategory>,
    fecha: String,
    onFechaChange: (String) -> Unit,
    descripcion: String = "",
    onDescripcionChange: (String) -> Unit = {},
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // MONTO
        SectionLabel(text = "MONTO")
        AmountCard(value = monto, onValueChange = onMontoChange, isError = montoError)

        Spacer(modifier = Modifier.height(24.dp))

        // ESTABLECIMIENTO
        SectionLabel(text = "ESTABLECIMIENTO / COMERCIO")
        TextField(
            value = establecimiento,
            onValueChange = onEstablecimientoChange,
            placeholder = "Ej: Starbucks, Coto...",
            isError = establecimientoError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CATEGORÍA
        SectionLabel(text = "CATEGORÍA")
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            TextField(
                value = if (categoria.isEmpty()) "Seleccionar categoría" else categoria,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                },
                isError = categoriaError,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                if (categoriesList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay categorías") },
                        onClick = { expandedCategory = false }
                    )
                } else {
                    categoriesList.forEach { category ->
                        DropdownMenuItem(
                            text = { 
                                Row {
                                    Text(text = category.icon)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = category.name)
                                }
                            },
                            onClick = {
                                onCategoriaChange(category.name)
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DESCRIPCIÓN (Nuevo)
        SectionLabel(text = "NOTAS / DESCRIPCIÓN (OPCIONAL)")
        TextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            placeholder = "Agregar una nota...",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // FECHA
        SectionLabel(text = "FECHA")
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = fecha,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
        
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = try {
                    LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onFechaChange(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        }
                        showDatePicker = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            text = buttonText,
            onClick = onButtonClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
