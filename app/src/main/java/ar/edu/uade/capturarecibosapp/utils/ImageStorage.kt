package ar.edu.uade.capturarecibosapp.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorage {
    /**
     * Guarda un bitmap en el almacenamiento interno y devuelve la ruta absoluta.
     */
    fun saveInternalImage(context: Context, bitmap: Bitmap): String? {
        val fileName = "ticket_${UUID.randomUUID()}.jpg"
        val directory = File(context.filesDir, "tickets")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
