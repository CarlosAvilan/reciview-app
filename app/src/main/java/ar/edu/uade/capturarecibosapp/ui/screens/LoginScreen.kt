package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.TextField
import ar.edu.uade.capturarecibosapp.ui.components.Button
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.LoginViewModel
import ar.edu.uade.capturarecibosapp.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel, 
    onLoginRedirect: () -> Unit, 
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    onClick = onForgotPasswordClick
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                text = "Iniciar sesión",
                onClick = { viewModel.login(onSuccess = onLoginRedirect) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text(
                    text = "¿No tienes una cuenta?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = " Regístrate",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ReciViewTheme {
        val viewModel = remember { LoginViewModel() }
        LoginScreen(
            viewModel = viewModel,
            onLoginRedirect = {},
            onRegisterClick = {}
        )
    }
}
