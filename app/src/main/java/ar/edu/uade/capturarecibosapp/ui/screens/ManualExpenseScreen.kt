package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopBar(title = "Cargar Gasto Manual", onBackClick = onBackClick)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ExpenseForm(
            modifier = Modifier.padding(paddingValues),
            monto = viewModel.monto,
            onMontoChange = { viewModel.onMontoChange(it) },
            montoError = viewModel.montoError,
            establecimiento = viewModel.establecimiento,
            onEstablecimientoChange = { viewModel.onEstablecimientoChange(it) },
            establecimientoError = viewModel.establecimientoError,
            categoria = viewModel.categoria,
            onCategoriaChange = { viewModel.onCategoriaChange(it) },
            categoriaError = viewModel.categoriaError,
            categoriesList = categories,
            fecha = viewModel.fecha,
            onFechaChange = { viewModel.onFechaChange(it) },
            buttonText = "Guardar Gasto",
            onButtonClick = { 
                viewModel.guardarGasto {
                    onSaveSuccess()
                }
            }
        )
    }
}
