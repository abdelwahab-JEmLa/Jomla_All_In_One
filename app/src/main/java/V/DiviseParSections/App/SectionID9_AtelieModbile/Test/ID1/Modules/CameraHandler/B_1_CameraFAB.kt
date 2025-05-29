package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

@Composable
fun B_1_CameraFAB(
    onCreateProductAndCapture: () -> A_ProduitInfosTest,
    onProductCreated: (A_ProduitInfosTest) -> Unit,
    size: Dp = 48.dp,
    containerColor: Color = Color(0xFF4CAF50),
    viewModel: ViewModel_TestID2 = koinInject(),
    webPQuality: Int = 85 // Paramètre qualité WebP seulement
) {
    val TAG = "CameraFAB"
    val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
        .child("Images Articles Data Base")
        .child("produits")
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/" +
                "Abdelwahab_jeMla.com" +
                "/IMGs" +
                "/BaseDonne"

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var pendingProduct by remember { mutableStateOf<A_ProduitInfosTest?>(null) }

    suspend fun forceImageRefreshWithDelay(
        viewModel: ViewModel_TestID2,
        productId: Long,
        delayMs: Long = 500L
    ) {
        repeat(3) { attempt ->
            delay(delayMs)
            viewModel.updateActualisationImage(productId)
            Log.d(TAG, "Force refresh attempt ${attempt + 1} for product $productId")
        }
    }

    suspend fun handleImageCaptureAndProductCreation(uri: Uri) {
        try {
            pendingProduct?.let { product ->
                Log.d(TAG, "Processing image for product: ${product.nom} (ID: ${product.id})")

                // Utiliser l'extension WebP au lieu de JPG
                val fileName = "${product.id}_1.webp"

                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) {
                        Log.d(TAG, "Creating directory: $absolutePath")
                        mkdirs()
                    }
                }

                val localFile = File(localStorageDir, fileName)
                var uploadSuccess = false

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()
                    Log.d(TAG, "WebP image bytes size: ${imageBytes.size}")

                    try {
                        withContext(Dispatchers.IO) {
                            FileOutputStream(localFile).use { output ->
                                output.write(imageBytes)
                                output.flush()
                            }
                        }

                        Log.d(TAG, "Local WebP file saved: ${localFile.absolutePath}")

                        if (!localFile.exists() || localFile.length() == 0L) {
                            throw Exception("Local WebP file verification failed")
                        }

                        val uploadTask = imagesProduitsFireBaseStorageRef
                            .child(fileName)
                            .putBytes(imageBytes)
                            .await()

                        if (uploadTask.metadata != null) {
                            uploadSuccess = true
                            Log.d(TAG, "Firebase WebP upload successful for $fileName")
                        }

                        if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                            withContext(Dispatchers.Main) {
                                delay(200)

                                val updatedProduct = product.copy(
                                    actualiseSonImage = 1,
                                    actualiseSonImageTest2 = 1,
                                    timestamps = System.currentTimeMillis(),
                                    needUpdate = true
                                )

                                Log.d(TAG, "Product updated with WebP image refresh counters")
                                onProductCreated(updatedProduct)

                                delay(300)
                                viewModel.updateActualisationImage(updatedProduct.id)
                                delay(200)
                                viewModel.updateActualisationImage(updatedProduct.id)

                                scope.launch {
                                    forceImageRefreshWithDelay(viewModel, updatedProduct.id)
                                }

                                Toast.makeText(
                                    context,
                                    "Produit WebP créé avec succès: ${updatedProduct.nom}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            throw Exception("WebP upload verification failed")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during WebP image processing", e)
                        if (localFile.exists() && !uploadSuccess) {
                            localFile.delete()
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Échec du téléchargement WebP: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        throw e
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleImageCaptureAndProductCreation", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors du traitement WebP: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } finally {
            pendingProduct = null
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val newProduct = onCreateProductAndCapture()
            pendingProduct = newProduct
            Log.d(TAG, "Created pending product: ${newProduct.nom} (ID: ${newProduct.id})")
            showCameraDialog = true
        } else {
            Toast.makeText(
                context,
                "Permission caméra requise",
                Toast.LENGTH_SHORT
            ).show()
            pendingProduct = null
        }
    }

    // Camera dialog
    if (showCameraDialog) {
        CameraXDialog(
            onImageCaptured = { uri ->
                showCameraDialog = false
                scope.launch {
                    handleImageCaptureAndProductCreation(uri)
                }
            },
            onDismiss = {
                showCameraDialog = false
                pendingProduct = null
            },
            webPQuality = webPQuality // Passer seulement la qualité WebP
        )
    }

    FloatingActionButton(
        onClick = {
            Log.d(TAG, "FAB clicked - starting WebP product creation process")
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        },
        modifier = Modifier.size(size),
        containerColor = containerColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit et prendre photo WebP"
        )
    }
}
