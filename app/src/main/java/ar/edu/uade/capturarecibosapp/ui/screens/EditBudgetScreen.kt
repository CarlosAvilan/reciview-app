package ar.edu.uade.capturarecibosapp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
                value = viewModel.budgetInput,
                onValueChange = { viewModel.onBudgetInputChange(it) },
                label = "Nuevo presupuesto mensual",
                placeholder = "Ej: 60000.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = viewModel.budgetError != null
            )

            if (viewModel.budgetError != null) {
                Text(
                    text = viewModel.budgetError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                text = "Guardar cambios",
                onClick = {
                    viewModel.updateBudget(onSaveSuccess)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditBudgetScreenPreview() {
    ReciViewTheme {
        val context = LocalContext.current
        val viewModel = remember { ProfileViewModel(context.applicationContext as Application) }
        EditBudgetScreen(
            viewModel = viewModel,
            onBackClick = {},
            onSaveSuccess = {}
        )
    }
}
