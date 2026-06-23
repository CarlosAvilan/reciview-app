package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.DateField
import ar.edu.uade.capturarecibosapp.ui.components.FieldErrorText
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.RegisterViewModel
import ar.edu.uade.capturarecibosapp.ui.viewmodel.RegisterState

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val context = LocalContext.current

    var showCountryDropdown by remember { mutableStateOf(false) }
    val countries = listOf("Argentina", "Brasil", "Chile", "Uruguay", "Colombia", "México")

    Scaffold(
        topBar = {
            TopBar(
                title = "Crear cuenta",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            TextButton(onClick = { /* Cambiar foto */ }) {
                Text(
                    "Cambiar foto",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = viewModel.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = "Nombre completo",
                isError = viewModel.nameError
            )
            FieldErrorText(if (viewModel.nameError) "El nombre no puede estar vacío" else null)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.correoElectronico,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = "Correo electrónico",
                isError = viewModel.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña",
                isPassword = true,
                isError = viewModel.passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateField(
                value = viewModel.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                label = "Fecha de nacimiento",
                isError = viewModel.birthDateError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // País de residencia
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = viewModel.paisNacimiento,
                    onValueChange = { },
                    label = "País de residencia",
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    readOnly = true,
                    isError = viewModel.countryError,
                    modifier = Modifier.clickable { showCountryDropdown = true }
                )
                
                Box(modifier = Modifier.matchParentSize().clickable { showCountryDropdown = true })

                DropdownMenu(
                    expanded = showCountryDropdown,
                    onDismissRequest = { showCountryDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.8f).background(MaterialTheme.colorScheme.surface)
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                viewModel.onPaisChange(country)
                                showCountryDropdown = false
                            }
                        )
                    }
                }
            }
            FieldErrorText(if (viewModel.countryError) "Seleccioná un país de residencia" else null)

            Spacer(modifier = Modifier.height(32.dp))

            // Términos y Condiciones con ClickableText
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append("Por favor, lea y acepte los ")
                    }
                    pushStringAnnotation(tag = "terms", annotation = "navigate")
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append("Términos y Condiciones")
                    }
                    pop()
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(" antes de continuar")
                    }
                }

                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset)
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

            if (viewModel.uiState is RegisterState.Loading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (viewModel.uiState is RegisterState.Error) {
                Text(
                    text = (viewModel.uiState as RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                text = "Registrarme",
                onClick = { viewModel.registrarse() },
                enabled = viewModel.haLeidoTerminos && viewModel.uiState !is RegisterState.Loading
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
            onTermsClick = {}
        )
    }
}
