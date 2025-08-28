package Z_CodePartageEntreApps.Modules.CameraHandler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.view.Surface
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Helper function to get orientation from URI
fun getOrientationFromUri(context: Context, uri: Uri): Int {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0
    } catch (e: Exception) {
        0
    }
}

// Helper function to rotate bitmap
fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    return if (degrees == 0f) {
        bitmap
    } else {
        val matrix = Matrix().apply { postRotate(degrees) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

// Helper function to convert YUV to RGB bitmap
fun convertYuvToRgb(imageProxy: ImageProxy): Bitmap? {
    return try {
        val yBuffer = imageProxy.planes[0].buffer // Y
        val vuBuffer = imageProxy.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}

// Helper function to convert bitmap to WebP bytes
fun bitmapToWebPBytes(bitmap: Bitmap, quality: Int): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val success = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, outputStream)
        }
        else -> {
            @Suppress("DEPRECATION")
            bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)
        }
    }

    return if (success) {
        outputStream.toByteArray()
    } else {
        // Fallback to JPEG if WebP fails
        val jpegStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, jpegStream)
        jpegStream.toByteArray()
    }
}

// Extension function for Bitmap scaling
fun Bitmap.scale(newWidth: Int, newHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}

// Updated CameraXDialog with proper orientation handling for direct camera capture
@Composable
fun CameraXDialog(
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit,
    webPQuality: Int = 85
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: Preview? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isCapturing by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Get current device rotation
    val rotation = remember(configuration.orientation) {
        when (configuration.orientation) {
            android.content.res.Configuration.ORIENTATION_PORTRAIT -> Surface.ROTATION_0
            android.content.res.Configuration.ORIENTATION_LANDSCAPE -> Surface.ROTATION_90
            else -> Surface.ROTATION_0
        }
    }

    suspend fun processImageToWebPWithOrientation(imageProxy: ImageProxy): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val bitmap = when (imageProxy.format) {
                    ImageFormat.JPEG -> {
                        val buffer = imageProxy.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    else -> convertYuvToRgb(imageProxy)
                }

                if (bitmap == null) return@withContext null

                // Apply rotation based on image proxy rotation info
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val correctedBitmap = if (rotationDegrees != 0) {
                    rotateBitmap(bitmap, rotationDegrees.toFloat()).also {
                        if (it !== bitmap) bitmap.recycle()
                    }
                } else bitmap

                // Scale if needed
                val maxSize = 2048
                val scaledBitmap = if (correctedBitmap.width > maxSize || correctedBitmap.height > maxSize) {
                    val scale = minOf(maxSize.toFloat() / correctedBitmap.width, maxSize.toFloat() / correctedBitmap.height)
                    val newWidth = (correctedBitmap.width * scale).toInt()
                    val newHeight = (correctedBitmap.height * scale).toInt()
                    correctedBitmap.scale(newWidth, newHeight).also {
                        if (it !== correctedBitmap) correctedBitmap.recycle()
                    }
                } else correctedBitmap

                val outputFile = File(context.cacheDir, "webp_${System.currentTimeMillis()}.webp")
                val webpBytes = bitmapToWebPBytes(scaledBitmap, webPQuality)

                FileOutputStream(outputFile).use { outputStream ->
                    outputStream.write(webpBytes)
                }

                scaledBitmap.recycle()

                if (outputFile.exists() && outputFile.length() > 0) {
                    Uri.fromFile(outputFile)
                } else {
                    outputFile.delete()
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    LaunchedEffect(lensFacing, rotation) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        try {
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder()
                .setTargetRotation(rotation)
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(95)
                .setTargetRotation(rotation) // Set target rotation for proper orientation
                .build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            cameraProvider?.let { provider ->
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                    isCameraReady = true
                } catch (exc: Exception) {
                    isCameraReady = false
                }
            }
        } catch (exc: Exception) {
            isCameraReady = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, "Fermer", tint = Color.White)
                    }

                    Row {
                        IconButton(
                            onClick = {
                                if (!isCapturing && isCameraReady) {
                                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                        CameraSelector.LENS_FACING_FRONT
                                    } else CameraSelector.LENS_FACING_BACK
                                    isCameraReady = false
                                }
                            },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.FlipCameraAndroid, "Changer", tint = Color.White)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    FloatingActionButton(
                        onClick = {
                            if (!isCapturing && isCameraReady && imageCapture != null) {
                                isCapturing = true
                                imageCapture?.takePicture(
                                    cameraExecutor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                val uri = processImageToWebPWithOrientation(imageProxy)
                                                imageProxy.close()
                                                isCapturing = false
                                                uri?.let { onImageCaptured(it) }
                                            }
                                        }
                                        override fun onError(exception: ImageCaptureException) {
                                            isCapturing = false
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        containerColor = when {
                            isCapturing -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            !isCameraReady -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = "Capturer WebP", modifier = Modifier.size(32.dp), tint = Color.White)
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
