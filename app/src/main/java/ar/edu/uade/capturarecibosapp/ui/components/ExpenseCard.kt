package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class ExpenseItem(
    val imageUrl: Int,
    val title: String,
    val date: String,
    val category: String,
    val amount: Double
)

@Composable
fun ExpenseCard(
    transaction: ExpenseItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Esquinas bien redondeadas como el diseño
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor de la foto (Carga desde URL)
            AsyncImage(
                model = transaction.imageUrl,
                contentDescription = "Logo de ${transaction.title}",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F4F7)), // Fondo gris placeholder mientras carga
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Textos centrales: Título y Subtítulo (Fecha • Categoría)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    color = Color(0xFF1A2536), // Azul oscuro/grisáceo de la imagen
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.date} • ${transaction.category}",
                    color = Color(0xFF6B7A99), // Gris intermedio para el subtítulo
                    fontSize = 14.sp
                )
            }

            // Monto a la derecha
            Text(
                text = "$${String.format("%,.0f", transaction.amount).replace(',', '.')}",
                color = Color(0xFF1A2536),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}