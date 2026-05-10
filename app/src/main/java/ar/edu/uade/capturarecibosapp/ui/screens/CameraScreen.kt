package ar.edu.uade.capturarecibosapp.ui.screens

import android.graphics.Bitmap
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CamaraScreen(onCapture: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            // Vinculamos al ciclo de vida para que la cámara se active
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. La vista de la cámara (Fondo)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. EL RECUADRO DE ENCUADRE (Overlay)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            val width = size.width
            val height = size.height
            val rectWidth = width * 0.8f  // El recuadro ocupa el 80% del ancho
            val rectHeight = height * 0.6f // Y el 60% del alto

            val left = (width - rectWidth) / 2
            val top = (height - rectHeight) / 2

            // Dibujamos un fondo semitransparente afuera del recuadro
            drawRect(
                color = Color.Black.copy(alpha = 0.5f)
            )

            // "Limpiamos" el centro para que se vea la cámara (Efecto marco)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                blendMode = BlendMode.Clear // Esto hace el "hueco"
            )

            // Dibujamos el borde blanco del recuadro
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                style = Stroke(width = 4.dp.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx())
            )
        }

        // 3. Botón de captura
        Button(
            onClick = {
                controller.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            onCapture(image.toBitmap())
                            image.close()
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp) // Subimos un poco el botón
        ) {
            Text("Escanear Ticket")
        }
    }
}