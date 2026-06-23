package ar.edu.uade.capturarecibosapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Extensiones para colores personalizados que no están en el ColorScheme estándar de Material3
val ColorScheme.success: Color get() = SuccessGreen
val ColorScheme.onSuccess: Color get() = Color.White

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = Pink80,
    background = Color(0xFF121212), // Fondo oscuro profundo
    surface = Color(0xFF333333),    // Superficie más clara para que las tarjetas resalten (Antes 0xFF2C2C2C)
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF454545), // Para elementos como contenedores de iconos
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFC2E8FF),
    errorContainer = Color(0xFF8B0000),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = Pink40,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = BluePrimary,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color.Gray,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = BluePrimary,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFEF5350)
)

@Composable
fun ReciViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
