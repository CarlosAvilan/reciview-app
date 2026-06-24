package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme

@Composable
fun ExpenseCard(
    title: String,
    date: String,
    category: String,
    categoryIcon: String? = null,
    amount: Double,
    photoUrl: String? = null,
    onAddTicketClick: () -> Unit = {}
) {
    Card(
        Modifier
            .fillMaxWidth(), RoundedCornerShape(24.dp), CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ), CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor de la foto o Botón "+"
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .then(if (photoUrl.isNullOrEmpty()) Modifier.clickable { onAddTicketClick() } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = photoUrl,
                        contentDescription = "Ticket",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Error al cargar ticket",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Asociar Ticket",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos centrales: Título y Subtítulo (Fecha • Categoría + Icono)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val categoryDisplay = if (categoryIcon != null) "$category " else category
                    Text(
                        text = "$date • $categoryDisplay",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (categoryIcon != null) {
                        Text(
                            text = categoryIcon,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            // Monto a la derecha
            Text(
                text = "$${String.format(Locale.getDefault(), "%,.0f", amount).replace(',', '.')}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,

            )
        }
    }
}
