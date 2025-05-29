package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun SimpleCameraCapture(
    onImageCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                imageCapture = ImageCapture.Builder().build()
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraX", "Use case binding failed", exc)
                }
            }, executor)
            
            previewView
        },
        modifier = modifier
    )
    
    // Bouton pour capturer
    FloatingActionButton(
        onClick = {
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            ).build()
            
            imageCapture?.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        output.savedUri?.let { onImageCaptured(it) }
                    }
                    
                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        }
    ) {
        Icon(Icons.Default.Camera, contentDescription = "Capture")
    }
}
