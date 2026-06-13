package ar.edu.uade.capturarecibosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.uade.capturarecibosapp.data.local.SessionManager
import ar.edu.uade.capturarecibosapp.navigation.AppNavigation
import ar.edu.uade.capturarecibosapp.navigation.Screen
import ar.edu.uade.capturarecibosapp.scanner.ScannerManager
import ar.edu.uade.capturarecibosapp.ui.components.BottomBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val scannerManager = ScannerManager(this) { bitmap ->
        viewModel.procesarImagen(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Chequeamos SharedPreferences antes de inflar Compose
        val sessionManager = SessionManager(this)
        val isLoggedIn = sessionManager.getUserId() != null

        setContent {
            ReciViewTheme {
                ReciViewApp(
                    viewModel = viewModel,
                    isLoggedIn = isLoggedIn,
                    onScanClick = { scannerManager.triggerScan() }
                )
            }
        }
    }
}

@Composable
fun ReciViewApp(
    viewModel: MainViewModel,
    isLoggedIn: Boolean,
    onScanClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomBarScreens

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            AppNavigation(
                navController = navController,
                mainViewModel = viewModel,
                startDestination = if (isLoggedIn) Screen.Welcome.route else Screen.Splash.route
            )

            // El cargador persiste hasta que la navegación se completa
            val isNavigatingToConfirm = viewModel.ticketDetectado != null && currentRoute != Screen.Confirmation.route
            
            if (viewModel.isProcessing || isNavigatingToConfirm) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analizando ticket...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
