package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    var showCountryDropdown by remember { mutableStateOf(false) }
    val countries = listOf("Argentina", "Brasil", "Chile", "Uruguay", "Colombia", "México")

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
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                label = "Fecha de nacimiento"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // País de nacimiento
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = viewModel.paisNacimiento,
                    onValueChange = { },
                    label = "País de nacimiento",
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    },
                    readOnly = true,
                    modifier = Modifier.clickable { showCountryDropdown = true }
                )

                // Capa invisible encima del TextField para capturar el click de forma más robusta
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showCountryDropdown = true }
                )

                DropdownMenu(
                    expanded = showCountryDropdown,
                    onDismissRequest = { showCountryDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                viewModel.onPaisChange(country)
                                showCountryDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Términos y Condiciones
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                val annotatedString = buildAnnotatedString {
                    append("Por favor, lea y acepte los ")
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        textDecoration = TextDecoration.Underline
                    )) {
                        append("Términos y Condiciones")
                    }
                    pop()
                    append(" antes de continuar")
                }

                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        annotatedString.getStringAnnotations(tag = "terms", start = 0, end = annotatedString.length)
                            .firstOrNull()?.let {
                                onTermsClick()
                            }
                    }
                )

                if (viewModel.haLeidoTerminos) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Términos aceptados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                text = "Registrarme",
                onClick = { viewModel.registrarse(onRegisterClick) },
                enabled = viewModel.haLeidoTerminos
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ReciViewTheme {
        val viewModel = remember { RegisterViewModel() }
        RegisterScreen(
            viewModel = viewModel,
            onBackClick = {},
            onRegisterClick = {},
            onTermsClick = {}
        )
    }
}
