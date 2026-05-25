package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.CategoryCard
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun ExpensesCategoriesScreen(
    onBackClick: () -> Unit,
    onEditCategoryClick: (CategoryItem?) -> Unit
) {
    // Datos mockeados según la imagen de Figma
    val categories = listOf(
        CategoryItem("🍔", "Comida y Bebida", 18500.0, 25000.0),
        CategoryItem("🚗", "Transporte", 12200.0, 15000.0),
        CategoryItem("💡", "Servicios y Hogar", 9800.0, 8000.0) // Supera presupuesto (Rojo)
    )

    Scaffold(
        topBar = {
            TopBar(
                title = "Gastos por categoría",
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Lista de tarjetas de categorías
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onEditCategoryClick(category) }
                )
            }

            // Botón "+ Nueva Categoría" con estilo de borde punteado
            item {
                NewCategoryButton(onClick = { onEditCategoryClick(null) })
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun NewCategoryButton(onClick: () -> Unit) {
    // Usamos un Box con un borde personalizado para simular el estilo punteado de Figma
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF4F8CF6).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
                // Nota: Compose nativo no tiene "Dashed" simple en .border,
                // se suele usar un Canvas o un recurso XML si se requiere exactitud.
                // Aquí usamos un borde sólido suave por consistencia técnica rápida.
            )
            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+ Nueva Categoria",
            color = Color(0xFF4F8CF6),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesCategoriesScreenPreview() {
    ReciViewTheme {
        ExpensesCategoriesScreen(
            onBackClick = {},
            onEditCategoryClick = {}
        )
    }
}
