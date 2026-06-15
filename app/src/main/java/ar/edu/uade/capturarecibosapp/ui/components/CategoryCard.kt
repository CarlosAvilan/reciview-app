package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// El "molde" para los datos de la categoría
data class CategoryItem(
    val icon: String,
    val name: String,
    val spent: Double,
    val budget: Double
)

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    val isOverBudget = category.spent > category.budget
    val accentColor = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val backgroundColor = if (isOverBudget) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
    val progress = if (category.budget > 0) (category.spent / category.budget).coerceIn(0.0, 1.0).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isOverBudget) Modifier.border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = category.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Texto de montos: se pone rojo si supera el presupuesto
                Text(
                    text = "$${String.format(Locale.getDefault(), "%,.0f", category.spent).replace(',', '.')} / $${String.format(Locale.getDefault(), "%,.0f", category.budget).replace(',', '.')}",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barra de progreso dinámica
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = accentColor,
                trackColor = if (isOverBudget) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
