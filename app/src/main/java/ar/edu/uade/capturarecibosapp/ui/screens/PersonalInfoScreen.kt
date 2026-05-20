package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.SectionLabel
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.PersonalInfoViewModel

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Información Personal",
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN PERFIL
            SectionLabel(text = "PERFIL")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JP",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF4F8CF6),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Campos Nombre y Email
                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = viewModel.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = "Nombre Completo"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Correo electrónico"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN DATOS DE CUENTA
            SectionLabel(text = "DATOS DE CUENTA")
            TextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = "Teléfono"
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                label = "Fecha de Nacimiento"
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.paisResidencia,
                onValueChange = { viewModel.onPaisChange(it) },
                label = "País de residencia",
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                },
                readOnly = true,
                modifier = Modifier.clickable { /* Abrir selector */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN SEGURIDAD
            SectionLabel(text = "SEGURIDAD")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { /* Navegar a cambio de pass */ },
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
                    Text(text = "Cambiar contraseña", style = MaterialTheme.typography.bodyLarge)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botones de acción
            Button(
                text = "Guardar Cambios",
                onClick = { viewModel.guardarCambios() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.eliminarCuenta() },
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
                    text = "Eliminar Cuenta",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonalInfoScreenPreview() {
    ReciViewTheme {
        PersonalInfoScreen(
            viewModel = PersonalInfoViewModel(),
            onBackClick = {}
        )
    }
}
