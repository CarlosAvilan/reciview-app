package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFF6B7280),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun AmountCard(value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
