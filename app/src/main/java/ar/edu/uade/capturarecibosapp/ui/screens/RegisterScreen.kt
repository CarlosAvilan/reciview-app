package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Crear cuenta",
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF4F46E5)
                )
            }

            TextButton(onClick = { /* Cambiar foto */ }) {
                Text(
                    "Cambiar foto",
                    color = Color(0xFF4F46E5),
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campos de texto usando los nuevos componentes reutilizables
            TextField(
                value = viewModel.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = "Nombre completo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.correoElectronico,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = "Correo electrónico"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = "Teléfono"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                label = "Fecha de nacimiento"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // País de residencia
            TextField(
                value = viewModel.paisResidencia,
                onValueChange = { viewModel.onPaisChange(it) },
                label = "País de residencia",
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                },
                readOnly = true,
                modifier = Modifier.clickable { /* Mostrar dropdown */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                text = "Registrarme",
                onClick = { viewModel.registrarse() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ReciViewTheme {
        RegisterScreen(
            viewModel = RegisterViewModel(),
            onBackClick = {}
        )
    }
}
