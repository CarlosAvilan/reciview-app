package ar.edu.uade.capturarecibosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.uade.capturarecibosapp.navigation.AppNavigation
import ar.edu.uade.capturarecibosapp.navigation.Screen
import ar.edu.uade.capturarecibosapp.scanner.ScannerManager
import ar.edu.uade.capturarecibosapp.ui.components.BottomBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // El gestor del escáner requiere el contexto de la Activity para registrar el launcher.
    private val scannerManager = ScannerManager(this) { bitmap ->
        viewModel.procesarImagen(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            ReciViewTheme {
                // Delegamos la estructura de la aplicación a un Composable independiente.
                ReciViewApp(
                    viewModel = viewModel,
                    onScanClick = { scannerManager.triggerScan() }
                )
            }
        }
    }
}

/**
 * Composable raíz que define la estructura visual de la aplicación (Shell UI).
 */
@Composable
fun ReciViewApp(
    viewModel: MainViewModel,
    onScanClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomBarScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute ?: Screen.Welcome.route,
                    onScanClick = onScanClick,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(
                navController = navController,
                startScan = onScanClick,
                mainViewModel = viewModel
            )
        }
    }
}
