package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.BottomBar
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseCard
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.ui.components.ExpenseItem
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun MyExpensesScreen(onScanClick: () -> Unit,
                     totalGastado: String = "$45.280,50",
                     estadistica: String = "+ 25% vs enero"
                     ) {


    val transacciones = listOf(
        ExpenseItem(
            imageUrl = R.drawable.logo_carrefour,
            title = "Carrefour Market",
            date = "Hoy, 14:20",
            category = "Alimentos",
            amount = 12400.0
        ),
        ExpenseItem(
            imageUrl = R.drawable.logo_uber,
            title = "Uber Trip",
            date = "Ayer, 21:15",
            category = "Transporte",
            amount = 5500.0
        ),
        ExpenseItem(
            imageUrl = R.drawable.logo_edesur,
            title = "Edesur",
            date = "18 May, 09:30",
            category = "Servicios",
            amount = 9800.0
        )
    )

    Scaffold(
        bottomBar = {
            BottomBar(onScanClick = onScanClick)
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        // Todo dentro del LazyColumn para que no se superpongan los elementos
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mis Gastos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF1A2536)
                )
            }

            // Tarjeta de Gasto Total
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4F8CF6))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Gasto Total del Mes",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = totalGastado,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        SuggestionChip(
                            onClick = { /* Opcional */ },
                            label = {
                                Text(
                                    text = estadistica,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            shape = CircleShape,
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            border = null // Eliminamos el borde por defecto de Material 3
                        )
                    }
                }
            }


            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tickets Recientes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A2536)
                    )
                    Text(
                        text = "Ver todo",
                        color = Color(0xFF4F46E5),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable() { /* Ver todo */ }
                    )
                }
            }

            // Lista dinámica de tarjetas de gastos
            items(transacciones) { transaccion ->
                ExpenseCard(transaction = transaccion)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Navegar a categorías */ },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gastos por categoría",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF6484E4),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF6484E4)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyExpensesScreenPreview() {
    ReciViewTheme {
        MyExpensesScreen(
            onScanClick = {}
        )
    }
}
