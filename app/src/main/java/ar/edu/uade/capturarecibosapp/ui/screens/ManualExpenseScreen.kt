package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ManualExpenseViewModel

@Composable
fun ManualExpenseScreen(viewModel: ManualExpenseViewModel, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(title = "Cargar Gasto Manual", onBackClick = onBackClick)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("MONTO")
            AmountCard(value = viewModel.monto)

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("ESTABLECIMIENTO / COMERCIO")
            TextField(
                value = viewModel.establecimiento,
                onValueChange = { viewModel.onEstablecimientoChange(it) },
                label = "Ej: Starbucks, Coto..."
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("CATEGORÍA")
            TextField(
                value = if (viewModel.categoria.isEmpty()) "Seleccionar categoría" else viewModel.categoria,
                onValueChange = { },
                label = "",
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                readOnly = true,
                modifier = Modifier.clickable { /* Abrir selector */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionLabel("FECHA")
            TextField(
                value = viewModel.fecha,
                onValueChange = { },
                label = "",
                readOnly = true,
                modifier = Modifier.clickable { /* Abrir date picker */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                text = "Guardar Gasto",
                onClick = { viewModel.guardarGasto() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFF6B7280),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AmountCard(value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManualExpenseScreenPreview() {
    ReciViewTheme {
        ManualExpenseScreen(
            viewModel = ManualExpenseViewModel(),
            onBackClick = {}
        )
    }
}
