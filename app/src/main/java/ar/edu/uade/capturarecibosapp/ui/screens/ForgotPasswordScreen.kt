package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ForgotPasswordStep
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackClick: () -> Unit,
    onCodeSent: () -> Unit = {}
) {
    // Escuchar el cambio de estado para navegar
    LaunchedEffect(viewModel.currentStep) {
        if (viewModel.currentStep == ForgotPasswordStep.VERIFY_CODE) {
            onCodeSent()
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Habilitar scroll por si aparece el teclado
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón volver
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Olvidé mi contraseña",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Ingresá tu mail para enviarte un código de seguridad.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Correo Electrónico",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // CORRECCIÓN: Usamos 'label' en lugar de 'placeholder'
            TextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "ejemplo@uade.edu.ar"
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                text = if (viewModel.isLoading) "Enviando..." else "Enviar Código",
                onClick = { viewModel.sendCode() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ReciViewTheme {
        ForgotPasswordScreen(
            viewModel = ForgotPasswordViewModel(),
            onBackClick = {}
        )
    }
}
