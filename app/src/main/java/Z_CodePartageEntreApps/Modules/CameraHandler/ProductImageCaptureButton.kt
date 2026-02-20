package Z_CodePartageEntreApps.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.net.Uri
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
    product: M01Produit,
    onImageCaptured: (M01Produit) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel = koinInject(),
    webPQuality: Int = 85
) {
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    // Store the created couleur keyID to use later
    var createdCouleurKeyID by remember { mutableStateOf<String?>(null) }

    fun getNextColorIndex(): Int {
        for (i in 1..4) {
            val colorValue = when (i) {
                1 -> product.couleur1
                2 -> product.couleur2
                3 -> product.couleur3
                4 -> product.couleur4
                else -> null
            }
            val imageFile = File("$localPath/${product.id}_$i.webp")
            if (colorValue.isNullOrBlank() || !imageFile.exists()) return i
        }
        return 1
    }

    suspend fun handleImageCapture(uri: Uri) {
        if (isProcessing) return
        isProcessing = true

        try {
            val colorIndex = getNextColorIndex()
            val fileName = "${product.id}_$colorIndex.webp"
            val localDir = File(localPath).apply { if (!exists()) mkdirs() }
            val localFile = File(localDir, fileName)
            var uploadSuccess = false

            context.contentResolver.openInputStream(uri)?.use { input ->
                val imageBytes = input.readBytes()

                try {
                    withContext(Dispatchers.IO) {
                        FileOutputStream(localFile).use { output ->
                            output.write(imageBytes)
                            output.flush()
                        }
                    }

                    if (!localFile.exists() || localFile.length() == 0L) {
                        throw Exception("Local WebP file verification failed")
                    }

                    val uploadTask = storageRef.child(fileName).putBytes(imageBytes).await()
                    if (uploadTask.metadata != null) uploadSuccess = true

                    if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                        withContext(Dispatchers.Main) {
                            delay(200)

                            // Use the previously created couleur keyID
                            val newCouleurKeyID = createdCouleurKeyID ?: ""

                            val updatedProduct = when (colorIndex) {
                                2 -> product.copy(couleur2 = newCouleurKeyID)
                                3 -> product.copy(couleur3 = newCouleurKeyID)
                                4 -> product.copy(couleur4 = newCouleurKeyID)
                                else -> product.copy(couleur1 = newCouleurKeyID)
                            }.copy(
                                actualiseSonImage = product.actualiseSonImage + 1,
                                actualiseSonImageTest2 = product.actualiseSonImageTest2 + 1,
                                dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
                            )

                            onImageCaptured(updatedProduct)

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
                    if (localFile.exists() && !uploadSuccess) localFile.delete()
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
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors du traitement WebP: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } finally {
            isProcessing = false
        }
    }

    fun handleAddNewM3Couleur(): String {
        val newCouleurP = M3CouleurProduitInfos
            .get_default()
            .copy(
                indexCouleurDansAncienProto = getNextColorIndex(),
                nomImageFichieSansEtansion = buildString {
                    append(product.id)
                    append("_")
                    append(getNextColorIndex())
                },
                parentBProduitOldID = product.id,
                parentBProduitInfosKeyID = product.keyID,
                parentId1ProduitInfosDebugName = product.nom,
                processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
            )

        viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
            newCouleurP
        )

        return newCouleurP.keyID
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            handleAddNewM3Couleur()
            showCameraDialog = true
        } else {
            Toast.makeText(context, "Permission caméra requise", Toast.LENGTH_SHORT).show()
        }
    }

    if (showCameraDialog) {
        CameraXDialog(
            onImageCaptured = { uri ->
                showCameraDialog = false
                scope.launch { handleImageCapture(uri) }
            },
            onDismiss = {
                showCameraDialog = false
                isProcessing = false
            },
            webPQuality = webPQuality
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable {
                if (!isProcessing) {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
            .padding(2.dp)
            .size(20.dp)
            .background(
                color = if (isProcessing) {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                },
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Ajouter une image WebP",
            tint = if (isProcessing) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(12.dp)
        )
    }
}
