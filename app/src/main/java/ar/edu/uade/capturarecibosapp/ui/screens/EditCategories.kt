package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun EditCategoriesScreen(
    category: CategoryItem? = null, // Recibe la categoría a editar (o null si es nueva)
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit
) {
    // Si venimos de una categoría existente, cargamos sus datos. Si no, usamos valores por defecto.
    var nombre by remember { mutableStateOf(category?.let { "${it.icon} ${it.name}" } ?: "") }
    var limite by remember { mutableStateOf(category?.let { "$${String.format("%,.0f", it.budget).replace(',', '.')}" } ?: "") }

    Scaffold(
        topBar = {
            TopBar(
                title = if (category == null) "Nueva categoría" else "Editar categoría",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                placeholder = { Text("Ej: 🍔 Comida") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Campo Límite
            OutlinedTextField(
                value = limite,
                onValueChange = { limite = it },
                label = { Text("Límite mensual sugerido") },
                placeholder = { Text("$0.00") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar
            Button(
                text = "Guardar datos",
                onClick = { onSaveClick(nombre, limite) }
            )

            // Botón Cancelar
            androidx.compose.material3.Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditCategoriesScreenPreview() {
    ReciViewTheme {
        EditCategoriesScreen(
            category = CategoryItem("🍔", "Comida y Bebida", 0.0, 25000.0),
            onBackClick = {},
            onSaveClick = { _, _ -> }
        )
    }
}
