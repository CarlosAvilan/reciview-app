package ar.edu.uade.capturarecibosapp.scanner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

/**
 * Contrato para el escaneo de recibos.
 * Sigue el principio de Inversión de Dependencias (D en SOLID).
 */
interface ReceiptScanner {

    /**
     * Registra y devuelve un ActivityResultLauncher configurado para procesar el resultado del escáner.
     * Debe llamarse durante la inicialización de la Activity/Fragment.
     */
    fun createLauncher(
        activity: ComponentActivity,
        onSuccess: (Bitmap) -> Unit,
        onError: (Exception) -> Unit
    ): ActivityResultLauncher<IntentSenderRequest>

    /**
     * Inicia el proceso de escaneo.
     */
    fun startScan(
        activity: ComponentActivity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onFailure: (Exception) -> Unit
    )

    /**
     * Obtiene la URI del resultado del escaneo a partir del intent de datos.
     */
    fun getScanUri(data: Intent?): Uri?

    /**
     * Procesa la URI resultante del escáner para convertirla en un Bitmap utilizable.
     */
    fun processResult(context: Context, uri: Uri): Bitmap
}
