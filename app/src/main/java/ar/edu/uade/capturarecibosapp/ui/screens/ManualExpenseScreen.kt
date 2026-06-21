package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseForm
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ManualExpenseViewModel

@Composable
fun ManualExpenseScreen(
    viewModel: ManualExpenseViewModel, 
    onBackClick: () -> Unit
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
            descripcion = viewModel.descripcion,
            onDescripcionChange = { viewModel.onDescripcionChange(it) },
            buttonText = "Guardar Gasto",
            onButtonClick = { 
                viewModel.guardarGasto()
            },
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading
        )
    }
}
