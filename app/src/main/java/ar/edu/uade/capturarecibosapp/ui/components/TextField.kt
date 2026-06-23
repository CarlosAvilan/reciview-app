package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        keyboardOptions = keyboardOptions,
        // Solo mostramos el label si no está vacío
        label = if (label.isNotEmpty()) { { Text(label) } } else null,
        // Agregamos el placeholder
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation =
            if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val icon =
                    if (passwordVisible)
                        Icons.Default.VisibilityOff
                    else
                        Icons.Default.Visibility
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                trailingIcon?.invoke()
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        readOnly = readOnly,
        singleLine = singleLine
    )
}
