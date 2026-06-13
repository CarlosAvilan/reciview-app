package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseForm
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun ConfirmationScreen(
    ticket: Ticket,
    onConfirm: (Ticket) -> Unit,
    onCancel: () -> Unit
) {
    var comercio by remember { mutableStateOf(ticket.establishment) }
    var total by remember { mutableStateOf(ticket.amount.toString()) }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("Hoy, 10 de Mayo") }

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
            establecimiento = comercio,
            onEstablecimientoChange = { comercio = it },
            categoria = categoria,
            onCategoriaClick = { /* Abrir selector de categoría */ },
            fecha = fecha,
            onFechaClick = { /* Abrir date picker */ },
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
