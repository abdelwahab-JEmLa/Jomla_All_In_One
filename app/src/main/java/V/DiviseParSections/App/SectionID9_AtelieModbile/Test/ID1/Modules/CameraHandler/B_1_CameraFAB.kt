package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun B_1_CameraFAB(
    onCreateProductForCapture: (() -> A_ProduitInfosTest)? = null,
    onImageUploadedToStorage: ((A_ProduitInfosTest) -> Unit)? = null,
    onProductCreated: ((A_ProduitInfosTest) -> Unit)? = null,
    productForCapture: A_ProduitInfosTest? = null,
    size: Dp = 48.dp,
    containerColor: Color = Color(0xFF4CAF50)
) {
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

    suspend fun handleImageCapture(uri: Uri) {
        try {
            if (uri.toString().isEmpty()) {
                throw IllegalArgumentException("Invalid URI")
            }

            pendingProduct?.let { product ->
                val fileName = "${product.id}_1.jpg"

                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) mkdirs()
                }

                val localFile = File(localStorageDir, fileName)
                var uploadSuccess = false

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()

                    try {
                        withContext(Dispatchers.IO) {
                            FileOutputStream(localFile).use { output ->
                                output.write(imageBytes)
                            }
                        }

                        val uploadTask = imagesProduitsFireBaseStorageRef
                            .child(fileName)
                            .putBytes(imageBytes)
                            .await()

                        if (uploadTask.metadata != null) {
                            uploadSuccess = true
                        }

                        if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                            val updatedProduct = product.copy(
                                articleHaveUniteImages = true,
                                needUpdate = true,
                                timestamps = System.currentTimeMillis(),
                                actualiseSonImage = product.actualiseSonImage + 1
                            )

                            onImageUploadedToStorage?.invoke(updatedProduct)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Image téléchargée avec succès pour ${product.nom}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            throw IOException("La vérification du téléchargement a échoué")
                        }
                    } catch (e: Exception) {
                        if (localFile.exists() && !uploadSuccess) {
                            localFile.delete()
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Échec du téléchargement de l'image: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        throw e
                    }
                }
            }
        } catch (e: Exception) {
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
            tempImageUri?.let { uri ->
                scope.launch {
                    handleImageCapture(uri)
                }
            }
        } else {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Échec de la capture d'image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            if (pendingProduct != null && tempImageUri != null) {
                cameraLauncher.launch(tempImageUri!!)
            }
        } else {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Permissions requises pour l'utilisation de la caméra",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun createTempImageUri(): Uri? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            ).also { tempImageUri = it }
        } catch (e: IOException) {
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
            permissionLauncher.launch(permissions)
        } else {
            val productToUse = when {
                onCreateProductForCapture != null -> {
                    val newProduct = onCreateProductForCapture.invoke()
                    onProductCreated?.invoke(newProduct)
                    newProduct
                }
                productForCapture != null -> {
                    productForCapture
                }
                else -> {
                    Toast.makeText(context, "Aucun produit disponible pour la photo", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            pendingProduct = productToUse
            createTempImageUri()?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
    }

    FloatingActionButton(
        onClick = { checkAndRequestPermissions() },
        modifier = Modifier.size(size),
        containerColor = containerColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit et prendre photo"
        )
    }
}
