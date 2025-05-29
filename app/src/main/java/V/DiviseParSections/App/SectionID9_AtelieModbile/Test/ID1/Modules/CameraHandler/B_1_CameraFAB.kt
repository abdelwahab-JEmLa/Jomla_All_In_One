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
import kotlinx.coroutines.CoroutineScope
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
    webPQuality: Int = 85
) {
    val TAG = "CameraFAB"
    val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
        .child("Images Articles Data Base")
        .child("produits")
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var pendingProduct by remember { mutableStateOf<A_ProduitInfosTest?>(null) }
    var isProcessing by remember { mutableStateOf(false) } // État pour éviter double traitement

    // Fonction optimisée pour le traitement d'image
    suspend fun handleImageCaptureOptimized(uri: Uri) {
        if (isProcessing) return // Éviter les traitements multiples
        isProcessing = true

        try {
            pendingProduct?.let { product ->
                Log.d(TAG, "Traitement image pour: ${product.nom} (ID: ${product.id})")

                val fileName = "${product.id}_1.webp"
                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) mkdirs()
                }
                val localFile = File(localStorageDir, fileName)

                // Lecture et traitement de l'image en une seule opération
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val imageBytes = inputStream.readBytes()
                        Log.d(TAG, "Image WebP: ${imageBytes.size} bytes")

                        // Sauvegarde locale immédiate
                        FileOutputStream(localFile).use { output ->
                            output.write(imageBytes)
                            output.flush()
                        }

                        // Upload Firebase en parallèle (ne pas attendre)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val uploadTask = imagesProduitsFireBaseStorageRef
                                    .child(fileName)
                                    .putBytes(imageBytes)
                                    .await()

                                if (uploadTask.metadata != null) {
                                    Log.d(TAG, "Upload Firebase réussi: $fileName")
                                } else {
                                    Log.w(TAG, "Upload Firebase incertain: $fileName")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Upload Firebase échoué: $fileName", e)
                            }
                        }

                        // Mise à jour immédiate du produit (ne pas attendre l'upload)
                        withContext(Dispatchers.Main) {
                            val updatedProduct = product.copy(
                                actualiseSonImage = 1,
                                actualiseSonImageTest2 = 1,
                                timestamps = System.currentTimeMillis(),
                                needUpdate = true
                            )

                            Log.d(TAG, "Produit mis à jour avec compteurs d'actualisation")
                            onProductCreated(updatedProduct)

                            // Actualisation de l'image en arrière-plan
                            scope.launch {
                                delay(100) // Délai minimal
                                viewModel.updateActualisationImage(updatedProduct.id)
                                delay(200)
                                viewModel.updateActualisationImage(updatedProduct.id)
                            }

                            Toast.makeText(
                                context,
                                "Produit WebP créé: ${updatedProduct.nom}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur traitement image", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur WebP: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } finally {
            isProcessing = false
            pendingProduct = null
        }
    }

    // Launcher de permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val newProduct = onCreateProductAndCapture()
            pendingProduct = newProduct
            Log.d(TAG, "Produit créé: ${newProduct.nom} (ID: ${newProduct.id})")
            showCameraDialog = true
        } else {
            Toast.makeText(context, "Permission caméra requise", Toast.LENGTH_SHORT).show()
            pendingProduct = null
        }
    }

    // Dialogue caméra optimisé
    if (showCameraDialog) {
        CameraXDialog(
            onImageCaptured = { uri ->
                showCameraDialog = false // Fermeture immédiate du dialogue
                Log.d(TAG, "Image reçue, fermeture dialogue")

                // Traitement en arrière-plan
                scope.launch {
                    handleImageCaptureOptimized(uri)
                }
            },
            onDismiss = {
                showCameraDialog = false
                pendingProduct = null
                isProcessing = false
            },
            webPQuality = webPQuality
        )
    }

    FloatingActionButton(
        onClick = {
            if (!isProcessing) { // Éviter les clics multiples
                Log.d(TAG, "FAB cliqué - démarrage processus WebP")
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        },
        modifier = Modifier.size(size),
        containerColor = if (isProcessing)
            containerColor.copy(alpha = 0.6f)
        else
            containerColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit WebP"
        )
    }
}
