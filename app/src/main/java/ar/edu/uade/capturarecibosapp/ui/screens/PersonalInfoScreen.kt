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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.DateField
import ar.edu.uade.capturarecibosapp.ui.components.FieldErrorText
import ar.edu.uade.capturarecibosapp.ui.components.LoadingOverlay
import ar.edu.uade.capturarecibosapp.ui.components.LoadingState
import ar.edu.uade.capturarecibosapp.ui.components.SectionLabel
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.PersonalInfoViewModel

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel = viewModel(),
    onBackClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Información Personal",
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
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.iniciales,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                        label = "Nombre Completo",
                        isError = viewModel.nameError != null
                    )
                    FieldErrorText(viewModel.nameError)
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
                label = "Teléfono",
                isError = viewModel.phoneError != null
            )
            FieldErrorText(viewModel.phoneError)
            Spacer(modifier = Modifier.height(16.dp))
            DateField(
                value = viewModel.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                label = "Fecha de Nacimiento",
                isError = viewModel.birthDateError != null
            )
            FieldErrorText(viewModel.birthDateError)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.paisResidencia,
                onValueChange = { viewModel.onPaisChange(it) },
                label = "País de residencia",
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Seleccionar país", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    .clickable { onChangePasswordClick() },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cambiar contraseña", 
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ir a cambiar contraseña",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
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

    // MODAL DE CONFIRMACIÓN
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(text = "Eliminar Cuenta")
            },
            text = {
                Text(text = "¿Estás seguro de que deseas eliminar tu cuenta de forma permanente? Se perderán todos tus gastos y preferencias guardadas. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.eliminarCuenta()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    // Spinner de carga
    when (viewModel.loadingState) {
        LoadingState.DELETING_ACCOUNT -> {
            LoadingOverlay(text = "Eliminando cuenta...")
        }
        LoadingState.SAVING_CHANGES -> {
            LoadingOverlay(text = "Guardando cambios...")
        }
        else -> {}
    }

    // MODAL DE ERROR (se muestra si algo falla)
    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null }, // Limpia el error al tocar fuera
            title = {
                Text(text = "Error")
            },
            text = {
                Text(text = viewModel.errorMessage!!)
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.errorMessage = null } // Limpia el error al aceptar
                ) {
                    Text(text = "Aceptar")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonalInfoScreenPreview() {
    ReciViewTheme {
        PersonalInfoScreen(
            onBackClick = {},
            onChangePasswordClick = {}
        )
    }
}
