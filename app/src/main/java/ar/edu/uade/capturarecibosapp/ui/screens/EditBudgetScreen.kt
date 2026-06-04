package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ProfileViewModel

@Composable
fun EditBudgetScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var tempBudget by remember { mutableStateOf(viewModel.presupuestoMensual) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Editar Presupuesto",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = tempBudget,
                onValueChange = { tempBudget = it },
                label = "Nuevo presupuesto mensual",
                placeholder = "Ej: 60.000,00"
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                text = "Guardar cambios",
                onClick = {
                    viewModel.updateBudget(tempBudget, onSaveSuccess)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditBudgetScreenPreview() {
    ReciViewTheme {
        val viewModel = remember { ProfileViewModel() }
        EditBudgetScreen(
            viewModel = viewModel,
            onBackClick = {},
            onSaveSuccess = {}
        )
    }
}
