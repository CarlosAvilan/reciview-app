package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.BottomBar
import ar.edu.uade.capturarecibosapp.ui.components.SectionLabel
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onPersonalInfoClick: () -> Unit,
    onCloseSessionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E7FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "JP",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = viewModel.nombre,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = viewModel.email,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // SECCIÓN CUENTA
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionLabel(text = "CUENTA")
            ProfileOptionRow(
                text = "Información Personal",
                onClick = onPersonalInfoClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

            // PREFERENCIAS
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionLabel(text = "PREFERENCIAS")
                ProfileValueRow(label = "Presupuesto mensual", value = viewModel.presupuestoMensual)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileSwitchRow(
                    label = "Notificaciones",
                    checked = viewModel.notificacionesEnabled,
                    onCheckedChange = { viewModel.onNotificacionesToggle(it) }
                )
            }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Cerrar Sesión estilizado
        Button(
            onClick = { viewModel.cerrarSesion(onCloseSessionClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFEBEE),
                contentColor = Color(0xFFEF5350)
            )
        ) {
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileOptionRow(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun ProfileValueRow(label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ProfileSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4F8CF6)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ReciViewTheme {
        ProfileScreen(
            viewModel = ProfileViewModel(),
            onPersonalInfoClick = {},
            onCloseSessionClick = {}
        )
    }
}
