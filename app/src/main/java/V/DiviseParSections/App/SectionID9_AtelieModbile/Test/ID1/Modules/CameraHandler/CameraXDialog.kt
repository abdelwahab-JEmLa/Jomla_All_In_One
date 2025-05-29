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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

@Composable
fun CameraXDialog(
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit,
    webPQuality: Int = 85 // Qualité WebP (0-100) - Toujours WebP maintenant
) {
    val TAG = "CameraXDialog"
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: Preview? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    // Fonction pour convertir ImageProxy en Bitmap
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // Fonction alternative pour YUV_420_888 format (plus robuste)
    fun imageProxyToBitmapYuv(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer // Y
        val vuBuffer = imageProxy.planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // Fonction simplifiée pour convertir ImageProxy en WebP uniquement
    suspend fun convertImageProxyToWebP(imageProxy: ImageProxy): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                // Conversion de ImageProxy en Bitmap avec gestion des différents formats
                val bitmap = try {
                    when (imageProxy.format) {
                        ImageFormat.JPEG -> imageProxyToBitmap(imageProxy)
                        ImageFormat.YUV_420_888 -> imageProxyToBitmapYuv(imageProxy)
                        else -> {
                            Log.w(TAG, "Unsupported format: ${imageProxy.format}, trying default conversion")
                            imageProxyToBitmap(imageProxy)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting ImageProxy to Bitmap", e)
                    return@withContext null
                }

                // Toujours créer un fichier WebP
                val outputFile = File(
                    context.cacheDir,
                    "captured_image_${System.currentTimeMillis()}.webp"
                )

                FileOutputStream(outputFile).use { outputStream ->
                    val success = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                            // API 30+ : Utiliser WEBP_LOSSY
                            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, webPQuality, outputStream)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                            // API 18+ : Utiliser WEBP (legacy)
                            @Suppress("DEPRECATION")
                            bitmap.compress(Bitmap.CompressFormat.WEBP, webPQuality, outputStream)
                        }
                        else -> {
                            // Fallback vers JPEG pour les versions très anciennes (renommer le fichier)
                            Log.w(TAG, "WebP not supported on this API level, falling back to JPEG")
                            val jpegFile = File(
                                context.cacheDir,
                                "captured_image_${System.currentTimeMillis()}.jpg"
                            )
                            outputFile.renameTo(jpegFile)
                            FileOutputStream(jpegFile).use { jpegStream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, webPQuality, jpegStream)
                            }
                        }
                    }

                    if (success) {
                        Log.d(TAG, "Image saved as WebP: ${outputFile.absolutePath}")
                        Uri.fromFile(outputFile)
                    } else {
                        Log.e(TAG, "Failed to compress image")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error converting image", e)
                null
            }
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
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    try {
                        cameraProvider = cameraProviderFuture.get()

                        preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        imageCapture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
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
                            } catch (exc: Exception) {
                                Log.e(TAG, "Use case binding failed", exc)
                            }
                        }
                    } catch (exc: Exception) {
                        Log.e(TAG, "CameraProvider initialization failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))
            }

            // Controls overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                            // Restart camera with new lens facing
                            cameraProvider?.let { provider ->
                                try {
                                    provider.unbindAll()
                                    val cameraSelector = CameraSelector.Builder()
                                        .requireLensFacing(lensFacing)
                                        .build()
                                    provider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageCapture
                                    )
                                } catch (exc: Exception) {
                                    Log.e(TAG, "Camera flip failed", exc)
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.FlipCameraAndroid,
                            contentDescription = "Changer caméra",
                            tint = Color.White
                        )
                    }
                }

                // Bottom capture button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            imageCapture?.let { capture ->
                                // Capture avec conversion WebP uniquement
                                capture.takePicture(
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                val convertedUri = convertImageProxyToWebP(imageProxy)
                                                imageProxy.close()

                                                convertedUri?.let { uri ->
                                                    Log.d(TAG, "WebP photo captured successfully: $uri")
                                                    onImageCaptured(uri)
                                                } ?: run {
                                                    Log.e(TAG, "Failed to convert image to WebP")
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Camera,
                            contentDescription = "Prendre photo",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Cleanup when composable is disposed
    LaunchedEffect(Unit) {
        return@LaunchedEffect
    }
}
