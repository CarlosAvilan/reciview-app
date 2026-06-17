package ar.edu.uade.capturarecibosapp.ui.screens

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
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ManualExpenseViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    viewModel.initializeWithTicket(ticket)

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
            establecimientoError = false,
            categoria = viewModel.categoria,
            onDescripcionChange = {viewModel.onDescripcionChange(it)},
            descripcion = viewModel.descripcion,
            onCategoriaChange = { viewModel.onCategoriaChange(it) },
            categoriaError = false,
            categoriesList = categories,
            fecha = viewModel.fecha,
            onFechaChange = { viewModel.onFechaChange(it) },
            buttonText = "Confirmar y Guardar",
            onButtonClick = {
                viewModel.guardarGasto { onConfirm() }
            }
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
            onConfirm = {},
            onCancel = {}
        )
    }
}
