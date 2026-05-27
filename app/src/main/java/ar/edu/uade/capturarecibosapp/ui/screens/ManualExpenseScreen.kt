package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseForm
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ManualExpenseViewModel

@Composable
fun ManualExpenseScreen(
    viewModel: ManualExpenseViewModel, 
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(title = "Cargar Gasto Manual", onBackClick = onBackClick)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        ExpenseForm(
            modifier = Modifier.padding(paddingValues),
            monto = viewModel.monto,
            onMontoChange = { viewModel.onMontoChange(it) },
            establecimiento = viewModel.establecimiento,
            onEstablecimientoChange = { viewModel.onEstablecimientoChange(it) },
            categoria = viewModel.categoria,
            onCategoriaClick = { /* Abrir selector */ },
            fecha = viewModel.fecha,
            onFechaClick = { /* Abrir date picker */ },
            buttonText = "Guardar Gasto",
            onButtonClick = { 
                viewModel.guardarGasto()
                onSaveSuccess()
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManualExpenseScreenPreview() {
    ReciViewTheme {
        ManualExpenseScreen(
            viewModel = ManualExpenseViewModel(),
            onBackClick = {},
            onSaveSuccess = {}
        )
    }
}
