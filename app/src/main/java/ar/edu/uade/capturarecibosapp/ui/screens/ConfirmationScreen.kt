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
    onConfirm: (Ticket) -> Unit,
    onCancel: () -> Unit,
    viewModel: ManualExpenseViewModel = viewModel()
) {
    var comercio by remember { mutableStateOf(ticket.establishment) }
    var total by remember { mutableStateOf(ticket.amount.toString()) }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("10/05/2026") } // Formato unificado

    val categories by viewModel.categories.collectAsState()

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
            monto = total,
            onMontoChange = { total = it },
            montoError = false,
            establecimiento = comercio,
            onEstablecimientoChange = { comercio = it },
            establecimientoError = false,
            categoria = categoria,
            onCategoriaChange = { categoria = it },
            categoriaError = false,
            categoriesList = categories,
            fecha = fecha,
            onFechaChange = { fecha = it },
            buttonText = "Confirmar y Guardar",
            onButtonClick = {
                onConfirm(
                    ticket.copy(
                        establishment = comercio,
                        amount = total.toFloatOrNull() ?: 0f
                    )
                )
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
