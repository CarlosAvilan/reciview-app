package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseForm
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ManualExpenseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ManualExpenseScreen(viewModel: ManualExpenseViewModel, onBackClick: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(title = "Cargar Gasto Manual", onBackClick = onBackClick)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                scope.launch {
                    // Mostramos el mensaje de confirmación
                    snackbarHostState.showSnackbar(
                        message = "Se ha guardado el nuevo gasto",
                        duration = SnackbarDuration.Short
                    )
                    // Esperamos 1 segundo para que el usuario lo lea antes de volver
                    delay(1000)
                    onBackClick() 
                }
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
            onBackClick = {}
        )
    }
}
