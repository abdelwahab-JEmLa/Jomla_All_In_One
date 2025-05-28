package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.IOException

@Composable
fun B_1_CameraFAB(
    onCreateProductAndCapture: () -> A_ProduitInfosTest,
    onProductCreated: (A_ProduitInfosTest) -> Unit,
    size: Dp = 48.dp,
    containerColor: Color = Color(0xFF4CAF50),
    viewModel: ViewModel_TestID2 = koinInject() // Inject ViewModel to trigger refresh
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
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingProduct by remember { mutableStateOf<A_ProduitInfosTest?>(null) }

     suspend fun forceImageRefreshWithDelay(
        viewModel: ViewModel_TestID2,
        productId: Long,
        delayMs: Long = 500L
    ) {
        repeat(3) { attempt ->
            delay(delayMs)
            viewModel.updateActualisationImage(productId)
            Log.d("CameraFAB", "Force refresh attempt ${attempt + 1} for product $productId")
        }
    }

    suspend fun handleImageCaptureAndProductCreation(uri: Uri) {
        try {
            if (uri.toString().isEmpty()) {
                throw IllegalArgumentException("Invalid URI")
            }

            pendingProduct?.let { product ->
                Log.d(TAG, "Processing image for product: ${product.nom} (ID: ${product.id})")
                val fileName = "${product.id}_1.jpg"

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
                    Log.d(TAG, "Image bytes size: ${imageBytes.size}")

                    try {
                        withContext(Dispatchers.IO) {
                            FileOutputStream(localFile).use { output ->
                                output.write(imageBytes)
                                output.flush() // Ensure data is written
                            }
                        }

                        Log.d(TAG, "Local file saved: ${localFile.absolutePath} (${localFile.length()} bytes)")

                        // Verify file was written correctly
                        if (!localFile.exists() || localFile.length().toInt() == 0) {
                            throw IOException("Local file verification failed")
                        }

                        val uploadTask = imagesProduitsFireBaseStorageRef
                            .child(fileName)
                            .putBytes(imageBytes)
                            .await()

                        if (uploadTask.metadata != null) {
                            uploadSuccess = true
                            Log.d(TAG, "Firebase upload successful for $fileName")
                        }

                        if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                            withContext(Dispatchers.Main) {
                                // CRITICAL FIX: Attendre un peu pour s'assurer que le fichier est complètement écrit
                                delay(200)

                                // Créer le produit avec un timestamp unique et des compteurs de refresh
                                val updatedProduct = product.copy(
                                    actualiseSonImage = 1, // Commencer à 1 pour forcer le chargement
                                    actualiseSonImageTest2 = 1,
                                    timestamps = System.currentTimeMillis(),
                                    needUpdate = true
                                )

                                Log.d(TAG, "Product updated with refresh counters: actualiseSonImage=${updatedProduct.actualiseSonImage}, actualiseSonImageTest2=${updatedProduct.actualiseSonImageTest2}")

                                // Ajouter le produit à l'UI
                                onProductCreated(updatedProduct)

                                // Attendre que l'UI soit mise à jour, puis forcer un refresh supplémentaire
                                delay(300)

                                // Double refresh pour s'assurer que l'image se charge
                                viewModel.updateActualisationImage(updatedProduct.id)

                                delay(200)

                                // Triple refresh si nécessaire (pour les cas difficiles)
                                viewModel.updateActualisationImage(updatedProduct.id)

                                scope.launch {
                                    forceImageRefreshWithDelay(viewModel, updatedProduct.id)
                                }
                                Log.d(TAG, "Product creation and refresh completed for ${updatedProduct.nom}")
                                Toast.makeText(context, "Produit créé avec succès: ${updatedProduct.nom}", Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            throw IOException("La vérification du téléchargement a échoué")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during image processing", e)
                        if (localFile.exists() && !uploadSuccess) {
                            localFile.delete()
                            Log.d(TAG, "Deleted incomplete local file")
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Échec du téléchargement de l'image: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        throw e
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleImageCaptureAndProductCreation", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Erreur lors du traitement de l'image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            pendingProduct = null
            tempImageUri = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d(TAG, "Camera capture successful")
            tempImageUri?.let { uri ->
                scope.launch {
                    handleImageCaptureAndProductCreation(uri)
                }
            }
        } else {
            Log.w(TAG, "Camera capture failed")
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Échec de la capture d'image", Toast.LENGTH_SHORT).show()
                }
            }
            // Clear pending product if capture failed
            pendingProduct = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d(TAG, "All permissions granted")
            if (pendingProduct != null && tempImageUri != null) {
                cameraLauncher.launch(tempImageUri!!)
            }
        } else {
            Log.w(TAG, "Permissions denied: $permissions")
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Permissions requises pour l'utilisation de la caméra",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            // Clear pending product if permissions denied
            pendingProduct = null
        }
    }

    fun createTempImageUri(): Uri? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            tempImageUri = uri
            Log.d(TAG, "Created temp image URI: $uri")
            uri
        } catch (e: IOException) {
            Log.e(TAG, "Failed to create temp image URI", e)
            null
        }
    }

    fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            Log.d(TAG, "Requesting permissions")
            permissionLauncher.launch(permissions)
        } else {
            // Create product but don't add to UI yet - wait for successful upload
            val newProduct = onCreateProductAndCapture()
            pendingProduct = newProduct
            Log.d(TAG, "Created pending product: ${newProduct.nom} (ID: ${newProduct.id})")

            createTempImageUri()?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
    }

    FloatingActionButton(
        onClick = {
            Log.d(TAG, "FAB clicked - starting product creation process")
            checkAndRequestPermissions()
        },
        modifier = Modifier.size(size),
        containerColor = containerColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit et prendre photo"
        )
    }
}
