package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.Views.Image_Displaye
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

@Composable
fun ColorImageCard_FragID3(
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    val isEditMode = focusedValuesGetter.active_Central_Values.affiche_buttons_lien_unite_couleur_au_couleut_parent
        || focusedValuesGetter.active_Central_Values.currentApp_Est_Admin

    val relative_M1produit = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
        repositorysMainGetter.repo1Produit.datasValue.find {
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

                        repositorysMainGetter.repo3CouleurProduit.addOrUpdateData(relative_M3CouleurProduitInfos.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "webp",
                            il_a_une_video_presentaion = false,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        repositorysMainGetter.repo1Produit.update(relative_M1produit.copy(
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
                        repositorysMainGetter.repo3CouleurProduit.addOrUpdateData(relative_M3CouleurProduitInfos.copy(
                            aAffiche = M3CouleurProduitInfos.Type.Image,
                            nomImageFichieSansEtansion = fileNameWithoutExtension,
                            extensionDisponible = "mp4",
                            il_a_une_video_presentaion = true,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        ))

                        repositorysMainGetter.repo1Produit.update(relative_M1produit.copy(
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
            // Barre media picker — affichée au-dessus de l'image en mode édition
            if (isEditMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End)
                ) {
                    Card(
                        modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "🖼",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Card(
                        modifier = Modifier.clickable { videoPickerLauncher.launch("video/*") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "🎥",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                    relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                    contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                    modifier = Modifier.Companion,
                )
            }
        }
    }
}
