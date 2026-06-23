package ar.edu.uade.capturarecibosapp.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseForm
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ManualExpenseViewModel

@Composable
fun ConfirmationScreen(
    ticket: Ticket,
    bitmap: Bitmap? = null,
    onCancel: () -> Unit,
    viewModel: ManualExpenseViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()

    // Inicializar el formulario solo cuando el ticket cambia, no en cada recomposición
    LaunchedEffect(ticket) {
        viewModel.initializeWithTicket(ticket, bitmap)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Confirmar Gasto",
                onBackClick = onCancel
            )
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
            onDescripcionChange = {viewModel.onDescripcionChange(it)},
            descripcion = viewModel.descripcion,
            onCategoriaChange = { viewModel.onCategoriaChange(it) },
            categoriaError = viewModel.categoriaError,
            categoriesList = categories,
            fecha = viewModel.fecha,
            onFechaChange = { viewModel.onFechaChange(it) },
            buttonText = "Confirmar y Guardar",
            onButtonClick = {
                viewModel.guardarGasto()
            },
            errorMessage = viewModel.errorMessage,
            isLoading = viewModel.isLoading
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConfirmationScreenPreview() {
    ReciViewTheme {
        ConfirmationScreen(
            ticket = Ticket(
                createdAt = "10 de mayo",
                userId = "",
                categoryId = 0,
                establishment = "Starbucks",
                amount = 1200f,
                photoUrl = "",
                description = ""
            ),
            onCancel = {}
        )
    }
}
