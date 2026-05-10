package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.data.model.TicketData

@Composable
fun ConfirmacionScreen(
    ticket: TicketData,
    onConfirm: (TicketData) -> Unit,
    onCancel: () -> Unit
) {
    var comercio by remember { mutableStateOf(ticket.comercio) }
    var total by remember { mutableStateOf(ticket.total.toString()) }
    var descripcion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Confirmar Datos del Ticket", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = comercio,
            onValueChange = { comercio = it },
            label = { Text("Nombre del Comercio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = total,
            onValueChange = { total = it },
            label = { Text("Total") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (Opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    onConfirm(
                        ticket.copy(
                            comercio = comercio,
                            total = total.toDoubleOrNull() ?: 0.0,
                            descripcion = descripcion
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}
