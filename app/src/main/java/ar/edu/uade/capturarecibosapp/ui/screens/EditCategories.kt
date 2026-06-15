package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import ar.edu.uade.capturarecibosapp.data.model.UserCategory
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.TextField as CustomTextField
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun EditCategoriesScreen(
    userCategory: UserCategory? = null, // Recibe la entidad UserCategory
    nameError: Boolean = false,
    budgetError: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String) -> Unit
) {
    // Cargamos datos desde UserCategory
    var nombre by remember { mutableStateOf(userCategory?.name ?: "") }
    var limite by remember { mutableStateOf(userCategory?.let { "$${String.format(Locale.getDefault(), "%,.0f", it.budget).replace(',', '.')}" } ?: "") }
    var selectedEmoji by remember { mutableStateOf(userCategory?.icon ?: "📁") }
    
    val emojis = listOf("🛒", "🚗", "🍔", "💡", "🎮", "🏥", "👔", "🏠", "✈️", "🎓")

    Scaffold(
        topBar = {
            TopBar(
                title = if (userCategory == null) "Nueva categoría" else "Editar categoría",
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
            CustomTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                placeholder = "Ej: Comida",
                isError = nameError
            )

            // Campo Límite
            CustomTextField(
                value = limite,
                onValueChange = { limite = it },
                label = "Límite mensual sugerido",
                placeholder = "$0.00",
                isError = budgetError
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Selector de Emoji (TAREA 3)
            Text(
                text = "Selecciona un icono",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(emojis) { emoji ->
                    val isSelected = selectedEmoji == emoji
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar
            Button(
                text = "Guardar datos",
                onClick = { onSaveClick(nombre, limite, selectedEmoji) }
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
            userCategory = UserCategory(name = "Comida", budget = 25000.0, userId = "123"),
            onBackClick = {},
            onSaveClick = { _, _, _ -> }
        )
    }
}
