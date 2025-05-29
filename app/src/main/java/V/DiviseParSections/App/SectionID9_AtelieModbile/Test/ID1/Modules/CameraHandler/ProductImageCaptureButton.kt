package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
fun ProductImageCaptureButton(
    product: A_ProduitInfosTest,
    onImageCaptured: (A_ProduitInfosTest) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewModel_TestID2 = koinInject(),
    webPQuality: Int = 85 // Paramètre qualité WebP seulement
) {
    val TAG = "ProductImageCaptureButton"
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

    // Calculate next color index but now check for WebP files
    fun getNextColorIndex(): Int {
        for (i in 1..4) {
            val colorValue = when (i) {
                1 -> product.couleur1
                2 -> product.couleur2
                3 -> product.couleur3
                4 -> product.couleur4
                else -> null
            }

            // Vérifier les fichiers WebP au lieu de JPG
            val imageFile = File("$imagesProduitsLocalExternalStorageBasePath/${product.id}_$i.webp")
            if (colorValue.isNullOrBlank() || !imageFile.exists()) {
                return i
            }
        }
        return 1 // Overwrite slot 1 if all are taken
    }

    suspend fun handleImageCaptureForProduct(uri: Uri) {
        try {
            Log.d(TAG, "Processing new WebP image for product: ${product.nom} (ID: ${product.id})")

            val colorIndex = getNextColorIndex()
            // Utiliser l'extension WebP au lieu de JPG
            val fileName = "${product.id}_$colorIndex.webp"

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
                                actualiseSonImage = product.actualiseSonImage + 1,
                                actualiseSonImageTest2 = product.actualiseSonImageTest2 + 1,
                                timestamps = System.currentTimeMillis(),
                                needUpdate = true
                            )

                            Log.d(TAG, "Product updated with new WebP image: color index $colorIndex")
                            onImageCaptured(updatedProduct)

                            delay(300)
                            viewModel.updateActualisationImage(updatedProduct.id)
                            delay(200)
                            viewModel.updateActualisationImage(updatedProduct.id)

                            Toast.makeText(
                                context,
                                "Nouvelle image WebP ajoutée pour ${updatedProduct.nom}",
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
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleImageCaptureForProduct", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors du traitement WebP: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraDialog = true
        } else {
            Toast.makeText(
                context,
                "Permission caméra requise",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Camera dialog with CameraX
    if (showCameraDialog) {
        CameraXDialog(
            onImageCaptured = { uri ->
                showCameraDialog = false
                scope.launch {
                    handleImageCaptureForProduct(uri)
                }
            },
            onDismiss = {
                showCameraDialog = false
            },
            webPQuality = webPQuality // Passer seulement la qualité WebP
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(2.dp)
            .size(20.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                Log.d(TAG, "WebP Camera button clicked for product: ${product.nom}")
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Ajouter une image WebP",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
        )
    }
}
