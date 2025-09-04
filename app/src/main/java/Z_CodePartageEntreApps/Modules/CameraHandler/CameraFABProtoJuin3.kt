package Z_CodePartageEntreApps.Modules.CameraHandler

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.content.Context
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

// TODO(1) Fix: Dynamic color index instead of hardcoded "1"

suspend fun addNew(
    product: ArticlesBasesStatsTable,
    context: Context,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    a_CentralCompoRepositoryProtoJuin9: RepositorysMainGetter,
    fileName: String,
    colorIndex: Int = 1 // Add colorIndex parameter
) {
    withContext(Dispatchers.Main) {
        val newOldId = repositorysMainGetter.repo1ProduitInfos.datasValue.maxOf { it.id } + 1
        val idParentCategorie =
            focusedValuesGetter.active_Central_Values.active_Catalogue_Pour_NewAddedProduit?.premierCategorieId
                ?: 0
        val keyIDM3CouleurProduitInfos = getPushFireBase(M3CouleurProduitInfos.ref)
        val keyID = getPushFireBase(ArticlesBasesStatsTable.ref)

        val currentValues = focusedValuesGetter.active_Central_Values
        val etateActuelle = if (currentValues.active_EtateDispoNonDifinieAuAddNew) {
            ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie
        } else {
            ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
        }

        val disponibilityState = if (currentValues.active_EtateDispoNonDifinieAuAddNew) {
            DisponibilityEtates.NON_DISPO
        } else {
            DisponibilityEtates.DISPO
        }

        val newProduit = product.copy(
            id = newOldId,
            keyID = keyID,
            nom = "Produit $idParentCategorie ${keyID.takeLast(4).uppercase()}",
            couleur1 = keyIDM3CouleurProduitInfos,
            actualiseSonImage = 1,
            actualiseSonImageTest2 = 1,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis(),
            etateActuelleOnFusionAvecBaseDonne = etateActuelle,
            disponibilityEtates = disponibilityState,
            idParentCategorie = idParentCategorie
        )

        a_CentralCompoRepositoryProtoJuin9.repo1ProduitInfos.upsert(newProduit)

        val newCouleurP = M3CouleurProduitInfos
            .get_default()
            .copy(
                keyID = keyIDM3CouleurProduitInfos,
                nomImageFichieSansEtansion = "${newOldId}_$colorIndex", // Use dynamic colorIndex
                parentBProduitInfosKeyID = newProduit.keyID,
                parentId1ProduitInfosDebugName = newProduit.nom,
                parentBProduitOldID = newProduit.id,
                indexCouleurDansAncienProto = colorIndex, // Set the proper index
                processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeAuGeneralHandler
            )

        aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
            newCouleurP
        )

        val statusMessage = if (currentValues.active_EtateDispoNonDifinieAuAddNew) {
            "Produit créé (état non défini): ${newProduit.nom}"
        } else {
            "Produit WebP créé: ${newProduit.nom}"
        }

        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CameraFABProtoJuin3(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    a_CentralCompoRepositoryProtoJuin9: RepositorysMainGetter = koinInject(),
    size: Dp = 48.dp,
    containerColor: Color = Color(0xFF4CAF50),
    webPQuality: Int = 85,
) {
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var pendingProduct by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    fun getNextColorIndexForNewProduct(): Int {
        // For new products, always start with color index 1
        return 1
    }

    suspend fun handleImageCapture(uri: Uri) {
        if (isProcessing) return
        isProcessing = true

        try {
            pendingProduct?.let { product ->
                val colorIndex = getNextColorIndexForNewProduct()
                val fileName = "${product.id}_$colorIndex.webp"
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
                            } catch (e: Exception) {
                                // Handle upload error but continue
                            }
                        }

                        addNew(
                            product,
                            context,
                            aCentralFacade,
                            a_CentralCompoRepositoryProtoJuin9 = a_CentralCompoRepositoryProtoJuin9,
                            fileName = fileName,
                            colorIndex = colorIndex // Pass the dynamic color index
                        )
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
                id = a_CentralCompoRepositoryProtoJuin9.repo1ProduitInfos.datasValue.maxOf { it.id } + 1
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
            modifier = Modifier,
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Créer produit WebP"
        )
    }
}
