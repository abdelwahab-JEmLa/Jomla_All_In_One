package Z_CodePartageEntreApps.Modules.CameraHandler

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne
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
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

@Composable
fun CameraFABProtoJuin3(
    a_CentralCompoRepositoryProtoJuin9: ACentralCompoRepositoryProtoJuin9 = koinInject(),
    size: Dp = 48.dp,
    containerColor: Color = Color(0xFF4CAF50),
    webPQuality: Int = 85,
    activeCatalogue: CataloguesCaegorie,
) {
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var pendingProduct by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    suspend fun handleImageCapture(uri: Uri) {
        if (isProcessing) return
        isProcessing = true

        try {
            pendingProduct?.let { product ->
                val fileName = "${product.id}_1.webp"
                val localDir = File(localPath).apply { if (!exists()) mkdirs() }
                val localFile = File(localDir, fileName)

                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val bytes = input.readBytes()

                        FileOutputStream(localFile).use { output ->
                            output.write(bytes)
                            output.flush()
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                storageRef.child(fileName).putBytes(bytes).await()
                            } catch (e: Exception) {}
                        }

                        withContext(Dispatchers.Main) {
                            val bsonObjectId = pendingProduct!!.bsonObjectId.takeLast(4).uppercase()
                            val catalogue = activeCatalogue.nom.takeLast(3)

                            val updatedProduct = product.copy(
                                nom = "Produit $catalogue id${pendingProduct!!.id} $bsonObjectId",
                                actualiseSonImage = 1,
                                actualiseSonImageTest2 = 1,
                                dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis(),
                                etateActuelleOnFusionAvecBaseDonne =  EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage,
                                idParentCategorie = activeCatalogue.premierCategorieId
                            )

                            a_CentralCompoRepositoryProtoJuin9.a_ProduitDataBaseComposeRepositoryPJ17.addOrUpdateData(updatedProduct)


                            Toast.makeText(context, "Produit WebP créé: ${updatedProduct.nom}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Erreur WebP: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            isProcessing = false
            pendingProduct = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingProduct = ArticlesBasesStatsTable(
                id = a_CentralCompoRepositoryProtoJuin9.a_ProduitDataBaseComposeRepositoryPJ17.datasValue.maxOf { it.id } + 1
            )
            showCameraDialog = true
        } else {
            Toast.makeText(context, "Permission caméra requise", Toast.LENGTH_SHORT).show()
            pendingProduct = null
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
                pendingProduct = null
                isProcessing = false
            },
            webPQuality = webPQuality
        )
    }

    FloatingActionButton(
        onClick = {
            if (!isProcessing) {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        },
        modifier = Modifier.size(size),
        containerColor = if (isProcessing) containerColor.copy(alpha = 0.6f) else containerColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit WebP"
        )
    }
}
