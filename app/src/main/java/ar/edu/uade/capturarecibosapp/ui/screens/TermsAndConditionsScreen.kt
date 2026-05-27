package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.RegisterViewModel

@Composable
fun TermsAndConditionsScreen(
    viewModel: RegisterViewModel,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Términos y Condiciones",
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
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Políticas y Privacidad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Por favor lee y acepta las condiciones para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Box con los términos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    TermItem(
                        title = "1. Procesamiento de Datos Seguro",
                        description = "Las imágenes de tus comprobantes se procesan de forma estrictamente local en el dispositivo utilizando el SDK avanzado de ML Kit."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TermItem(
                        title = "2. Almacenamiento en la Nube",
                        description = "Una vez extraídos los montos, los datos de consumo se sincronizarán de forma segura en tu base de datos para habilitar reportes."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TermItem(
                        title = "3. Permisos de Hardware",
                        description = "Requerimos acceso a la cámara exclusivamente para capturar las imágenes de tus recibos."
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Checkboxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.terminosAceptados,
                    onCheckedChange = { viewModel.onTerminosAceptadosChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Acepto los Términos de Servicio y Privacidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.permisosCamaraAceptados,
                    onCheckedChange = { viewModel.onPermisosCamaraAceptadosChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Permito el uso de la cámara para escaneo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                text = "Continuar",
                onClick = {
                    viewModel.marcarTerminosComoLeidos()
                    onBackClick()
                },
                enabled = viewModel.terminosAceptados && viewModel.permisosCamaraAceptados
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TermItem(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TermsAndConditionsScreenPreview() {
    ReciViewTheme {
        val viewModel = remember { RegisterViewModel() }
        TermsAndConditionsScreen(
            viewModel = viewModel,
            onBackClick = {}
        )
    }
}
