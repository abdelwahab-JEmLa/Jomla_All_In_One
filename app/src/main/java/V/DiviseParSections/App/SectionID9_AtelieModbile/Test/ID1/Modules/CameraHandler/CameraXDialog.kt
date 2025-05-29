package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraXDialog(
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit,
    webPQuality: Int = 85
) {
    val TAG = "CameraXDialog"
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: Preview? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isCapturing by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }

    // Executor pour les opérations caméra
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // CORRECTION 1: Conversion YUV simplifiée et plus rapide
    fun convertYuvToRgb(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()

        // CORRECTION: Utiliser une qualité réduite pour la conversion temporaire
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 90, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // CORRECTION 2: Méthode de capture optimisée avec gestion d'erreurs améliorée
    suspend fun processImageToWebP(imageProxy: ImageProxy): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Début traitement image - Format: ${imageProxy.format}")

                val bitmap = when (imageProxy.format) {
                    ImageFormat.JPEG -> {
                        Log.d(TAG, "Traitement format JPEG")
                        val buffer = imageProxy.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    else -> {
                        Log.d(TAG, "Traitement format YUV")
                        convertYuvToRgb(imageProxy)
                    }
                }

                if (bitmap == null) {
                    Log.e(TAG, "Bitmap null après décodage")
                    return@withContext null
                }

                // CORRECTION 3: Réduire la taille si nécessaire pour améliorer les performances
                val maxSize = 2048
                val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
                    val scale = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
                    val newWidth = (bitmap.width * scale).toInt()
                    val newHeight = (bitmap.height * scale).toInt()

                    Log.d(TAG, "Redimensionnement: ${bitmap.width}x${bitmap.height} -> ${newWidth}x${newHeight}")
                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true).also {
                        if (it !== bitmap) bitmap.recycle()
                    }
                } else {
                    bitmap
                }

                // CORRECTION 4: Nom de fichier plus simple et création sécurisée
                val timestamp = System.currentTimeMillis()
                val outputFile = File(context.cacheDir, "webp_$timestamp.webp")

                Log.d(TAG, "Création fichier WebP: ${outputFile.absolutePath}")

                // CORRECTION 5: Compression WebP simplifiée avec fallback
                val success = try {
                    FileOutputStream(outputFile).use { outputStream ->
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, webPQuality, outputStream)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                                @Suppress("DEPRECATION")
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP, webPQuality, outputStream)
                            }
                            else -> {
                                // Fallback vers JPEG pour les très anciennes versions
                                Log.w(TAG, "WebP non supporté, utilisation JPEG")
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, webPQuality, outputStream)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erreur compression", e)
                    false
                }

                scaledBitmap.recycle()

                if (success && outputFile.exists() && outputFile.length() > 0) {
                    Log.d(TAG, "Fichier créé avec succès: ${outputFile.length()} bytes")
                    Uri.fromFile(outputFile)
                } else {
                    Log.e(TAG, "Échec création fichier")
                    outputFile.delete()
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur traitement image", e)
            null
        }
    }

    // CORRECTION 6: Initialisation de la caméra améliorée
    LaunchedEffect(lensFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        try {
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()

            // CORRECTION 7: Configuration ImageCapture optimisée
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(95) // Qualité élevée pour une meilleure base
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            cameraProvider?.let { provider ->
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    isCameraReady = true
                    Log.d(TAG, "Caméra initialisée avec succès")
                } catch (exc: Exception) {
                    Log.e(TAG, "Erreur binding caméra", exc)
                    isCameraReady = false
                }
            }
        } catch (exc: Exception) {
            Log.e(TAG, "Erreur initialisation caméra", exc)
            isCameraReady = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { previewView ->
                preview?.setSurfaceProvider(previewView.surfaceProvider)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                    ) {
                        Icon(Icons.Default.Close, "Fermer", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            if (!isCapturing && isCameraReady) {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                                isCameraReady = false // Réinitialisation nécessaire
                            }
                        },
                        modifier = Modifier.background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                    ) {
                        Icon(Icons.Default.FlipCameraAndroid, "Changer", tint = Color.White)
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            // CORRECTION 8: Vérifications avant capture
                            if (!isCapturing && isCameraReady && imageCapture != null) {
                                isCapturing = true
                                Log.d(TAG, "Début capture...")

                                imageCapture?.takePicture(
                                    cameraExecutor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                            Log.d(TAG, "Image capturée, traitement...")

                                            CoroutineScope(Dispatchers.Main).launch {
                                                val uri = processImageToWebP(imageProxy)
                                                imageProxy.close()

                                                isCapturing = false

                                                uri?.let {
                                                    Log.d(TAG, "Succès: $it")
                                                    onImageCaptured(it)
                                                } ?: run {
                                                    Log.e(TAG, "Échec traitement")
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e(TAG, "Erreur capture: ${exception.message}", exception)
                                            isCapturing = false
                                        }
                                    }
                                )
                            } else {
                                Log.w(TAG, "Capture impossible - isCapturing:$isCapturing, isCameraReady:$isCameraReady")
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        containerColor = when {
                            isCapturing -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            !isCameraReady -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ) {
                        Icon(
                            Icons.Default.Camera,
                            contentDescription = "Capturer WebP",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
        }
    }
}
