package Application4.App.Fragment.View.Components.A_Header.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.Views.Image_Displaye
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.res.painterResource
import Application2.App.View.Pro0.Proto.ViewS.getPrixDrawables
import java.io.File
import java.io.FileOutputStream

@Composable
fun ColorImageCard_App4(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,
    header: @Composable () -> Unit = {}
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val isEditMode = affiche_buttons_lien_unite_couleur_au_couleut_parent

    val relative_M1produit = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
        viewModel.active_Datas.list_M1Produit?.find {
            it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && relative_M1produit != null) {
            scope.launch {
                try {
                    val fileNameWithoutExtension = if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion.isNotBlank() && relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
                        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion
                    } else {
                        "${relative_M1produit.id}_${relative_M3CouleurProduitInfos.indexCouleurDansAncienProto}"
                    }

                    if (relative_M3CouleurProduitInfos.extensionDisponible.isNotBlank()) {
                        val oldFile = File(localPath, "${fileNameWithoutExtension}.${relative_M3CouleurProduitInfos.extensionDisponible}")
                        if (oldFile.exists()) {
                            oldFile.delete()
                        }
                    }

                    val contentResolver = context.contentResolver
                    val imageBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (imageBytes != null) {
                        val newFile = File(localPath, "${fileNameWithoutExtension}.webp")
                        withContext(Dispatchers.IO) {
                            FileOutputStream(newFile).use { it.write(imageBytes) }
                            try {
                                storageRef.child("${fileNameWithoutExtension}.webp").putBytes(imageBytes).await()
                            } catch (e: Exception) {
                                // silent upload error
                            }
                        }

                        viewModel.update_m3couleur(relative_M3CouleurProduitInfos.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "webp",
                            il_a_une_video_presentaion = false,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        viewModel.update_m1Produit(relative_M1produit.copy(
                            actualiseSonImage = relative_M1produit.actualiseSonImage + 1,
                            actualiseSonImageTest2 = relative_M1produit.actualiseSonImageTest2 + 1,
                            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                        ))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Image de couleur mise à jour !", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && relative_M1produit != null) {
            scope.launch {
                try {
                    val fileNameWithoutExtension = if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion.isNotBlank() && relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
                        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion
                    } else {
                        "${relative_M1produit.id}_${relative_M3CouleurProduitInfos.indexCouleurDansAncienProto}"
                    }

                    if (relative_M3CouleurProduitInfos.extensionDisponible.isNotBlank()) {
                        val oldFile = File(localPath, "${fileNameWithoutExtension}.${relative_M3CouleurProduitInfos.extensionDisponible}")
                        if (oldFile.exists()) {
                            oldFile.delete()
                        }
                    }

                    val newFile = File(localPath, "${fileNameWithoutExtension}.mp4")

                    val success = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(newFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                            newFile.exists()
                        } ?: false
                    }

                    if (success) {
                        viewModel.update_m3couleur(relative_M3CouleurProduitInfos.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "mp4",
                            il_a_une_video_presentaion = true,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        viewModel.update_m1Produit(relative_M1produit.copy(
                            actualiseSonImage = relative_M1produit.actualiseSonImage + 1,
                            actualiseSonImageTest2 = relative_M1produit.actualiseSonImageTest2 + 1,
                            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                        ))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Vidéo mise à jour !", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        androidx.compose.foundation.layout.Column {
            header()
            Box(
                modifier = if (isSelected) {
                    Modifier.Companion
                        .fillMaxWidth()
                        .aspectRatio(370.dp / 500.dp)
                } else {
                    Modifier.Companion
                        .fillMaxWidth()
                        .wrapContentHeight()
                }
            ) {
                Image_Displaye(
                    modifier = Modifier.Companion,
                    relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                    contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    list_M1Produit = uiState_NewProtoPatterns_viewModel.second.active_Datas
                        .list_M1Produit,
                )
                if (isSelected) {
                    val price = relative_M1produit?.clientPrixVentUnite ?: 0.0
                    val drawables = getPrixDrawables(price.toInt())
                    if (drawables.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(if (isEditMode) Alignment.Companion.TopStart else Alignment.Companion.TopEnd)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            drawables.forEachIndexed { index, res ->
                                Image(
                                    painter = painterResource(id = res),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .offset(x = (index * 14).dp, y = (index * 14).dp)
                                )
                            }
                        }
                    }
                }
                if (isSelected && isEditMode) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Companion.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Card(
                            modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = "🖼",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Card(
                            modifier = Modifier.clickable { videoPickerLauncher.launch("video/*") },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = "🎥",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
