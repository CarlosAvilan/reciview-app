package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExpenseForm(
    monto: String,
    onMontoChange: (String) -> Unit,
    establecimiento: String,
    onEstablecimientoChange: (String) -> Unit,
    categoria: String,
    onCategoriaClick: () -> Unit,
    fecha: String,
    onFechaClick: () -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        AmountCard(value = monto)

        Spacer(modifier = Modifier.height(24.dp))

        // ESTABLECIMIENTO
        SectionLabel(text = "ESTABLECIMIENTO / COMERCIO")
        TextField(
            value = establecimiento,
            onValueChange = onEstablecimientoChange,
            label = "Ej: Starbucks, Coto..."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CATEGORÍA
        SectionLabel(text = "CATEGORÍA")
        TextField(
            value = if (categoria.isEmpty()) "Seleccionar categoría" else categoria,
            onValueChange = { },
            label = "",
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            },
            readOnly = true,
            modifier = Modifier.clickable { onCategoriaClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // FECHA
        SectionLabel(text = "FECHA")
        TextField(
            value = fecha,
            onValueChange = { },
            label = "",
            readOnly = true,
            modifier = Modifier.clickable { onFechaClick() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            text = buttonText,
            onClick = onButtonClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
