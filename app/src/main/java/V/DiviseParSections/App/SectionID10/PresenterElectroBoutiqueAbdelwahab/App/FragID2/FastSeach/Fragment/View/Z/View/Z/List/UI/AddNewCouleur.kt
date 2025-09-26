package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.ColorNameDropdownTextField
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraXDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
fun AddNewCouleur(
    modifier: Modifier = Modifier,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    size: Dp = 120.dp,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos = viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
    webPQuality: Int = 85
) {
    var isEditing by remember { mutableStateOf(false) }
    var colorName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    // Camera dialog state
    var showCameraDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Firebase and storage references
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    fun getNextColorIndex(): Int {
        val existingColors = viewModel.aCentralFacade.repositorysMainGetter
            .repo03CouleurProduitInfos.datasValue
            .filter { it.parentBProduitOldID == produit.id }

        for (i in 1..9) {
            val exists = existingColors.any { it.indexCouleurDansAncienProto == i }
            if (!exists) return i
        }
        return existingColors.size + 1
    }

    fun handleAddNewCouleur() {
        if (colorName.isNotBlank()) {
            val colorIndex = getNextColorIndex()

            val newCouleur = M3CouleurProduitInfos.get_default().copy(
                aAffiche = M3CouleurProduitInfos.Type.Nom,
                nomCouleurStrSiSonImageDispo = colorName.trim(),
                indexCouleurDansAncienProto = colorIndex,
                parentBProduitOldID = produit.id,
                parentBProduitInfosKeyID = produit.keyID,
                parentId1ProduitInfosDebugName = produit.nom,
                processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
            )

            viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
                newCouleur
            )

            val updatedProduit = when (colorIndex) {
                1 -> produit.copy(couleur1 = newCouleur.keyID)
                2 -> produit.copy(couleur2 = newCouleur.keyID)
                3 -> produit.copy(couleur3 = newCouleur.keyID)
                4 -> produit.copy(couleur4 = newCouleur.keyID)
                5 -> produit.copy(couleur5 = newCouleur.keyID)
                6 -> produit.copy(couleur6 = newCouleur.keyID)
                7 -> produit.copy(couleur7 = newCouleur.keyID)
                8 -> produit.copy(couleur8 = newCouleur.keyID)
                9 -> produit.copy(couleur9 = newCouleur.keyID)
                else -> produit // fallback, should not happen with getNextColorIndex logic
            }

            repositorysMainSetter.upsert_M1Produit(updatedProduit)

            colorName = ""
            isEditing = false

            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    suspend fun handleImageCapture(uri: Uri) {
        if (isProcessing) return
        isProcessing = true

        try {
            val colorIndex = getNextColorIndex()
            val fileName = "${produit.id}_$colorIndex.webp"
            val localDir = File(localPath).apply { if (!exists()) mkdirs() }
            val localFile = File(localDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                val imageBytes = input.readBytes()

                withContext(Dispatchers.IO) {
                    // Save locally first
                    FileOutputStream(localFile).use { output ->
                        output.write(imageBytes)
                        output.flush()
                    }

                    // UploadHandler to Firebase in background
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            storageRef.child(fileName).putBytes(imageBytes).await()
                        } catch (e: Exception) {
                            // Handle upload error silently - local file is still available
                        }
                    }
                }

                // Create new couleur with image type
                val newCouleur = M3CouleurProduitInfos.get_default().copy(
                    aAffiche = M3CouleurProduitInfos.Type.Image,
                    nomCouleurStrSiSonImageDispo = "", // Empty name for image-based color
                    nomImageFichieSansEtansion = "${produit.id}_$colorIndex",
                    indexCouleurDansAncienProto = colorIndex,
                    parentBProduitOldID = produit.id,
                    parentBProduitInfosKeyID = produit.keyID,
                    parentId1ProduitInfosDebugName = produit.nom,
                    processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
                )

                viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
                    newCouleur
                )

                // Update product with new color
                val updatedProduit = when (colorIndex) {
                    1 -> produit.copy(couleur1 = newCouleur.keyID)
                    2 -> produit.copy(couleur2 = newCouleur.keyID)
                    3 -> produit.copy(couleur3 = newCouleur.keyID)
                    4 -> produit.copy(couleur4 = newCouleur.keyID)
                    5 -> produit.copy(couleur5 = newCouleur.keyID)
                    6 -> produit.copy(couleur6 = newCouleur.keyID)
                    7 -> produit.copy(couleur7 = newCouleur.keyID)
                    8 -> produit.copy(couleur8 = newCouleur.keyID)
                    9 -> produit.copy(couleur9 = newCouleur.keyID)
                    else -> produit
                }.copy(
                    actualiseSonImage = produit.actualiseSonImage + 1,
                    actualiseSonImageTest2 = produit.actualiseSonImageTest2 + 1,
                    dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                )

                repositorysMainSetter.upsert_M1Produit(updatedProduit)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Nouvelle couleur image ajoutée pour ${produit.nom}",
                        Toast.LENGTH_SHORT
                    ).show()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors de l'ajout de la couleur: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } finally {
            isProcessing = false
            showCameraDialog = false
        }
    }

    fun handleAddCameraColor() {
        showCameraDialog = true
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            handleAddCameraColor()
        } else {
            Toast.makeText(context, "Permission caméra requise", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = modifier
            .size(size)
            .clickable {
                if (!isEditing && !isProcessing) {
                    isEditing = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isEditing) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        border = if (isEditing) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Camera button at top start
            if (!isEditing) {
                IconButton(
                    onClick = {
                        if (!isProcessing) {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Ajouter couleur par photo",
                        tint = if (isProcessing) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Camera dialog
            if (showCameraDialog) {
                CameraXDialog(
                    onImageCaptured = { uri ->
                        scope.launch { handleImageCapture(uri) }
                    },
                    onDismiss = {
                        showCameraDialog = false
                        isProcessing = false
                    },
                    webPQuality = webPQuality
                )
            }

            if (isEditing) {
                ColorNameDropdownTextField(
                    value = colorName,
                    onValueChange = { colorName = it },
                    placeholder = "Nom couleur",
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { handleAddNewCouleur() }
                    ),
                    repo03CouleurProduitInfos = repo03CouleurProduitInfos,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    onColorSelected = { selectedColorName ->
                        // Automatically save the color when selected from dropdown
                        colorName = selectedColorName
                        handleAddNewCouleur()
                    }
                )

                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        focusRequester.requestFocus()
                    }
                }

                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        delay(30000) // 30 seconds timeout
                        if (colorName.isBlank()) {
                            isEditing = false
                        }
                    }
                }
            } else {
                Text(
                    text = if (isProcessing) "Traitement..." else "nouvelle\ncouleur",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (isProcessing) 0.6f else 1f
                    ),
                    modifier = Modifier.graphicsLayer {
                        rotationZ = if (isProcessing) 0f else 45f
                    },
                    maxLines = 2
                )
            }
        }
    }
}
