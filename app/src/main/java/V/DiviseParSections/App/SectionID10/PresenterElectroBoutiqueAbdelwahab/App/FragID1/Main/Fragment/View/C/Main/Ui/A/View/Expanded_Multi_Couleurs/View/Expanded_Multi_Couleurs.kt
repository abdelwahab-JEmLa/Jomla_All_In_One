package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View

// AJOUT: Import du HeadViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.d.updateExpandedCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject
import java.io.File

@Composable
fun Expanded_Multi_Couleurs(
    relative_M1produit: ArticlesBasesStatsTable,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    var top_presanted_prisipame_couleur by remember { mutableStateOf(0) }

    // Sync avec expanded_M3CouleurProduitInfos du payload
    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    LaunchedEffect(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            // Vérifier si cette couleur appartient au produit actuel
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )

                if (matchingIndex != -1 && matchingIndex != top_presanted_prisipame_couleur) {
                    top_presanted_prisipame_couleur = matchingIndex
                }
            }
        }
    }

    fun onClick_Icon(relative_M3CouleurProduitInfos: M3CouleurProduitInfos) {
        // Utiliser la fonction toggle pour mettre à jour
        updateExpandedCouleur(
            relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
            focusedValuesGetter=focusedValuesGetter,
            on_pour_send_data = on_pour_send_data
        )

        on_pour_send_data(
            WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran.prefix,
            relative_M3CouleurProduitInfos.keyID
        )
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val selectedCouleur = relative_ListM3Couleurs[top_presanted_prisipame_couleur]

        ColorImageCard(
            relative_M3CouleurProduitInfos = selectedCouleur,
            isSelected = true,
            onIconClick = {
                onClick_Icon(selectedCouleur)
            },
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (relative_ListM3Couleurs.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                    if (index != top_presanted_prisipame_couleur) {
                        ColorImageCard(
                            relative_M3CouleurProduitInfos = couleur,
                            isSelected = false,
                            onIconClick = {
                                top_presanted_prisipame_couleur = index
                                // Mettre à jour l'état local
                                onClick_Icon(couleur)
                            },
                            on_pour_send_data = on_pour_send_data,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Trouve l'index de la couleur correspondante par correspondance directe uniquement.
 * Recherche par: keyID, index, et nom de couleur
 */
private fun findMatchingColorIndex(
    expandedColor: M3CouleurProduitInfos,
    availableColors: List<M3CouleurProduitInfos>
): Int {
    // Tentative 1: correspondance exacte par keyID
    val exactMatch = availableColors.indexOfFirst { it.keyID == expandedColor.keyID }
    if (exactMatch != -1) return exactMatch

    // Tentative 2: correspondance par index dans l'ancien proto
    val indexMatch = availableColors.indexOfFirst {
        it.parentBProduitOldID == expandedColor.parentBProduitOldID &&
                it.indexCouleurDansAncienProto == expandedColor.indexCouleurDansAncienProto
    }
    if (indexMatch != -1) return indexMatch

    // Tentative 3: correspondance par nom de couleur
    if (expandedColor.nomCouleurStrSiSonImageDispo.isNotBlank()) {
        val colorNameMatch = availableColors.indexOfFirst {
            it.nomCouleurStrSiSonImageDispo.equals(
                expandedColor.nomCouleurStrSiSonImageDispo,
                ignoreCase = true
            )
        }
        if (colorNameMatch != -1) return colorNameMatch
    }

    // Aucune correspondance trouvée
    return -1
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorImageCard(
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    onIconClick: () -> Unit,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageFile = remember(relative_M3CouleurProduitInfos.nomImageFichieSansEtansion, relative_M3CouleurProduitInfos.extensionDisponible) {
        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else {
            null
        }
    }

    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (isSelected) 370.dp / 500.dp else 100.dp / 60.dp)
        ) {
            if (imageFile != null && imageFile.exists()) {
                GlideImage(
                    model = imageFile,
                    contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = !isSelected) {
                            updateExpandedCouleur(
                                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                                focusedValuesGetter=focusedValuesGetter,
                                on_pour_send_data = on_pour_send_data
                            )
                        },
                    contentScale = if (isSelected) ContentScale.Fit else ContentScale.Crop
                ) {
                    it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize())
            }

            if (!isSelected) {
                IconButton(
                    onClick = onIconClick, // Cela appellera maintenant viewModel.onColorSelectedByHost()
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.7f),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Expand,
                        contentDescription = "Expand color details",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.6f))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(Priority.NORMAL)
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(400, 400)
    .disallowHardwareConfig()
    .format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565)
    .encodeQuality(70)
    .skipMemoryCache(false)
