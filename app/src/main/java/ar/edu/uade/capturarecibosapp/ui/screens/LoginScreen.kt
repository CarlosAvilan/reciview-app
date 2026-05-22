package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.LoginViewModel
import ar.edu.uade.capturarecibosapp.R

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    Scaffold(
        containerColor = Color(0xFFF2F2F2)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(4.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )

            Text(
                text = "Tu gestor de recibos inteligente",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = viewModel.correoElectronico,
                onValueChange = viewModel::onCorreoElectronicoChange,
                label = "Correo Electrónico"
            )

            TextField(
                value = viewModel.contrasenia,
                onValueChange = viewModel::onContraseniaChange,
                label = "Contraseña",
                isPassword = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { }
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFF4F46E5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                text = "Iniciar sesión",
                onClick = { viewModel.login() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text(
                    text = "¿No tienes una cuenta?",
                    color = Color.Gray
                )

                Text(
                    text = " Regístrate",
                    color = Color(0xFF4F8CF6),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ReciViewTheme {
        LoginScreen(
            viewModel = LoginViewModel()
        )
    }
}
