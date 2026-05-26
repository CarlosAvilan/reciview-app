package ar.edu.uade.capturarecibosapp.scanner

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.ComponentActivity

/**
 * Clase encargada de gestionar el ciclo de vida y la ejecución del escáner.
 * Encapsula el launcher y la lógica de disparo para mantener la Activity limpia.
 */
class ScannerManager(
    private val activity: ComponentActivity,
    private val scanner: ReceiptScanner = GoogleReceiptScanner(),
    private val onBitmapReady: (Bitmap) -> Unit
) {
    // El launcher se registra durante la inicialización de esta clase (que ocurre en la creación de la Activity)
    private val scannerLauncher = scanner.createLauncher(
        activity = activity,
        onSuccess = { bitmap ->
            onBitmapReady(bitmap)
        },
        onError = { e ->
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    )

    /**
     * Inicia el flujo de escaneo delegando en la implementación correspondiente.
     */
    fun triggerScan() {
        scanner.startScan(
            activity = activity,
            launcher = scannerLauncher,
            onFailure = { e ->
                Toast.makeText(activity, "Error al iniciar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
