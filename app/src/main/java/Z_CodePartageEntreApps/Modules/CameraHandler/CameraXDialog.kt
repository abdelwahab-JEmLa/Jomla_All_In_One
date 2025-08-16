package Z_CodePartageEntreApps.Modules.CameraHandler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.core.graphics.scale
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
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: Preview? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isCapturing by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    suspend fun processGalleryImageToWebP(selectedUri: Uri): Uri? {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap == null) return@withContext null

                val maxSize = 2048
                val scaledBitmap = if (originalBitmap.width > maxSize || originalBitmap.height > maxSize) {
                    val scale = minOf(maxSize.toFloat() / originalBitmap.width, maxSize.toFloat() / originalBitmap.height)
                    val newWidth = (originalBitmap.width * scale).toInt()
                    val newHeight = (originalBitmap.height * scale).toInt()
                    originalBitmap.scale(newWidth, newHeight).also {
                        if (it !== originalBitmap) originalBitmap.recycle()
                    }
                } else originalBitmap

                val outputFile = File(context.cacheDir, "gallery_webp_${System.currentTimeMillis()}.webp")

                val success = try {
                    FileOutputStream(outputFile).use { outputStream ->
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, webPQuality, outputStream)
                            }
                            true -> {
                                @Suppress("DEPRECATION")
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP, webPQuality, outputStream)
                            }
                            else -> {
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, webPQuality, outputStream)
                            }
                        }
                    }
                } catch (e: Exception) { false }

                scaledBitmap.recycle()

                if (success && outputFile.exists() && outputFile.length() > 0) {
                    Uri.fromFile(outputFile)
                } else {
                    outputFile.delete()
                    null
                }
            }
        } catch (e: Exception) { null }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            CoroutineScope(Dispatchers.Main).launch {
                val processedUri = processGalleryImageToWebP(selectedUri)
                processedUri?.let { onImageCaptured(it) }
            }
        }
    }

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
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 90, out)
        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
    }

    suspend fun processImageToWebP(imageProxy: ImageProxy): Uri? {
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

                val maxSize = 2048
                val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
                    val scale = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
                    val newWidth = (bitmap.width * scale).toInt()
                    val newHeight = (bitmap.height * scale).toInt()
                    bitmap.scale(newWidth, newHeight).also {
                        if (it !== bitmap) bitmap.recycle()
                    }
                } else bitmap

                val outputFile = File(context.cacheDir, "webp_${System.currentTimeMillis()}.webp")

                val success = try {
                    FileOutputStream(outputFile).use { outputStream ->
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, webPQuality, outputStream)
                            }
                            true -> {
                                @Suppress("DEPRECATION")
                                scaledBitmap.compress(Bitmap.CompressFormat.WEBP, webPQuality, outputStream)
                            }
                            else -> {
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, webPQuality, outputStream)
                            }
                        }
                    }
                } catch (e: Exception) { false }

                scaledBitmap.recycle()

                if (success && outputFile.exists() && outputFile.length() > 0) {
                    Uri.fromFile(outputFile)
                } else {
                    outputFile.delete()
                    null
                }
            }
        } catch (e: Exception) { null }
    }

    LaunchedEffect(lensFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        try {
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(95)
                .build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            cameraProvider?.let { provider ->
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                    isCameraReady = true
                } catch (exc: Exception) { isCameraReady = false }
            }
        } catch (exc: Exception) { isCameraReady = false }
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
                        // Gallery selection button
                        IconButton(
                            onClick = {
                                if (!isCapturing) {
                                    galleryLauncher.launch("image/*")
                                }
                            },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, "Galerie", tint = Color.White)
                        }

                        // Camera flip button
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
                                                val uri = processImageToWebP(imageProxy)
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
