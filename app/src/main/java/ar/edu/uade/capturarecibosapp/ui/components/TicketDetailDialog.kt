package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage

@Composable
fun TicketDetailDialog(
    commerce: String,
    date: String,
    amount: String,
    category: String,
    description: String,
    photoUrl: String?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalle del Ticket",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
<<<<<<< Updated upstream
                if (!photoUrl.isNullOrEmpty()) {

                    Box(
=======
                
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Imagen del recibo de $commerce",
>>>>>>> Stashed changes
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = photoUrl,
                            contentDescription = commerce,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            error = {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "X",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.5f
                                        ),
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                DetailRow(label = "Comercio", value = commerce)
                DetailRow(label = "Fecha", value = date)
                DetailRow(label = "Categoría", value = category)
                DetailRow(label = "Descripción", value = description.ifEmpty { "Sin descripción" })
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = amount,
                        fontWeight = FontWeight.Bold, 
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
