package ar.edu.uade.capturarecibosapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import ar.edu.uade.capturarecibosapp.ui.screens.*
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.MainViewModel
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ForgotPasswordViewModel
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ForgotPasswordStep
import ar.edu.uade.capturarecibosapp.ui.viewmodel.LoginViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    // Registrador para el resultado del escáner de Google
    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanResult?.pages?.get(0)?.imageUri?.let { uri ->
                try {
                    val bitmap = loadBitmapFromUri(uri)
                    viewModel.procesarImagen(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReciViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estados de navegación simples para el prototipo
                    var currentScreen by remember { mutableStateOf("login") }
                    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }

                    val ticket = viewModel.ticketDetectado

                    // Si hay un ticket detectado, mostramos la pantalla de confirmación (Flujo de Cámara)
                    if (ticket != null) {
                        ConfirmationScreen(
                            ticket = ticket,
                            onConfirm = { ticketEditado ->
                                viewModel.confirmarYSubir(ticketEditado)
                                Toast.makeText(this, "Ticket guardado", Toast.LENGTH_SHORT).show()
                            },
                            onCancel = { viewModel.cancelarCaptura() }
                        )
                    } else {
                        // Navegación entre las pantallas manuales
                        when (currentScreen) {
                            "login" -> {
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onForgotPasswordClick = { currentScreen = "forgot_password" },
                                    onLoginSuccess = { currentScreen = "welcome" }
                                )
                            }
                            "forgot_password" -> {
                                when (forgotPasswordViewModel.currentStep) {
                                    ForgotPasswordStep.EMAIL -> {
                                        ForgotPasswordScreen(
                                            viewModel = forgotPasswordViewModel,
                                            onBackClick = { currentScreen = "login" }
                                        )
                                    }
                                    ForgotPasswordStep.VERIFY_CODE -> {
                                        VerifyCodeScreen(
                                            viewModel = forgotPasswordViewModel,
                                            onBackClick = { forgotPasswordViewModel.backToEmail() }
                                        )
                                    }
                                    ForgotPasswordStep.NEW_PASSWORD -> {
                                        ResetPasswordScreen(
                                            viewModel = forgotPasswordViewModel,
                                            onBackClick = { forgotPasswordViewModel.backToVerifyCode() }
                                        )
                                    }
                                    ForgotPasswordStep.SUCCESS -> {
                                        PasswordSuccessScreen(
                                            onLoginClick = { 
                                                currentScreen = "login"
                                                // Reset ViewModel for next time
                                                forgotPasswordViewModel.backToEmail()
                                            }
                                        )
                                    }
                                }
                            }
                            "welcome" -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    WelcomeScreen(
                                        userName = "Juan",
                                        onScanClick = { startScan() },
                                        onCategoriesClick = { currentScreen = "categories" },
                                        onReportsClick = { currentScreen = "reports" },
                                        onHelpClick = { currentScreen = "help" },
                                        onTicketsClick = { currentScreen = "tickets" },
                                        onProfileClick = { /* Navegar a perfil si existiera una ruta */ },
                                        onExpensesClick = { /* Navegar a gastos */ }
                                    )

                                    if (viewModel.isProcessing) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                            "categories" -> {
                                ExpensesCategoriesScreen(
                                    onBackClick = { currentScreen = "welcome" },
                                    onEditCategoryClick = { category ->
                                        selectedCategory = category
                                        currentScreen = "edit_category"
                                    }
                                )
                            }
                            "edit_category" -> {
                                EditCategoriesScreen(
                                    category = selectedCategory,
                                    onBackClick = { currentScreen = "categories" },
                                    onSaveClick = { _, _ ->
                                        // Volvemos a la lista de categorías después de guardar
                                        currentScreen = "categories"
                                    }
                                )
                            }
                            "reports" -> {
                                ReportsScreen(
                                    onBackClick = { currentScreen = "welcome" }
                                )
                            }
                            "help" -> {
                                HelpScreen(
                                    onBackClick = { currentScreen = "welcome" }
                                )
                            }
                            "tickets" -> {
                                TicketsScreen(
                                    onScanClick = { startScan() },
                                    onHomeClick = { currentScreen = "welcome" },
                                    onExpensesClick = { /* Navegar a gastos */ },
                                    onProfileClick = { /* Navegar a perfil */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startScan() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(1)
            .setResultFormats(RESULT_FORMAT_JPEG)
            .setScannerMode(SCANNER_MODE_FULL)
            .build()

        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }
}
