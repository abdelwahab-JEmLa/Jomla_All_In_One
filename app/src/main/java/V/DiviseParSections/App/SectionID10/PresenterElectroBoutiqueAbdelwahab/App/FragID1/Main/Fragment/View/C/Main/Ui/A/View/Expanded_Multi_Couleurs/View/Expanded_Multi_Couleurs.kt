package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.compose.koinInject
import java.io.File

@Composable
fun Expanded_Multi_Couleurs(
    relative_M1produit: ArticlesBasesStatsTable,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    // Get the list of colors for this product
    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    // State to track the currently selected color (index in the list)
    var top_presanted_prisipame_couleur by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Top large image - the selected color
        val selectedCouleur = relative_ListM3Couleurs[top_presanted_prisipame_couleur]

        ColorImageCard(
            couleur = selectedCouleur,
            isSelected = true,
            onClick = { /* Already selected, no action */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom row with remaining colors
        if (relative_ListM3Couleurs.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                    // Skip the currently selected color in the bottom row
                    if (index != top_presanted_prisipame_couleur) {
                        ColorImageCard(
                            couleur = couleur,
                            isSelected = false,
                            onClick = {
                                // Swap: clicked color moves to top
                                top_presanted_prisipame_couleur = index
                            },
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

@Composable
private fun ColorImageCard(
    couleur: M3CouleurProduitInfos,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate the scale and elevation
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        animationSpec = spring(),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = spring(),
        label = "elevation"
    )

    // Build the file path for the image
    val imageFile = remember(couleur.nomImageFichieSansEtansion, couleur.extensionDisponible) {
        if (couleur.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${couleur.nomImageFichieSansEtansion}.${couleur.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else {
            null
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(enabled = !isSelected) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageFile != null && imageFile.exists()) {
                // Use Coil's AsyncImage for loading images from File
                AsyncImage(
                    model = imageFile,
                    contentDescription = couleur.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback for when image is not available
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
