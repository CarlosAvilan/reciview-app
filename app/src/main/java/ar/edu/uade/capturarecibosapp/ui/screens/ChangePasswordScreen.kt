package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(viewModel: ChangePasswordViewModel = viewModel(), onBackClick: () -> Unit, onSaveClick: () -> Unit) {

    Scaffold(
        topBar = {
            TopBar(
                title = "Cambiar contraseña",
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

            // Icono
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            //Campos de texto
            TextField(
                value = viewModel.contraseniaAnterior,
                onValueChange = { viewModel.onContraseniaAnteriorChange(it) },
                label = "Contraseña actual",
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = viewModel.nuevaContrasenia,
                onValueChange = { viewModel.onNuevaContraseniaChange(it) },
                label = "Nueva contraseña",
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.nuevaContraseniaRepetida,
                onValueChange = { viewModel.onNuevaContraseniaRepetidaChange(it) },
                label = "Repetir nueva contraseña",
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                text = "Guardar cambios",
                onClick = { viewModel.cambiarContrasenia(onSaveClick) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    ReciViewTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
