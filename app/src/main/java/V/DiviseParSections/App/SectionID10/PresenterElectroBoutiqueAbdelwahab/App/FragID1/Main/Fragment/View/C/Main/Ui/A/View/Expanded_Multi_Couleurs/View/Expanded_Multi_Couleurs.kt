package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
    modifier: Modifier = Modifier
) {
    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    var top_presanted_prisipame_couleur by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val selectedCouleur = relative_ListM3Couleurs[top_presanted_prisipame_couleur]

        ColorImageCard(
            couleur = selectedCouleur,
            isSelected = true,
            onIconClick = { },
            modifier = Modifier
                .fillMaxWidth()
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
                            couleur = couleur,
                            isSelected = false,
                            onIconClick = {
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorImageCard(
    couleur: M3CouleurProduitInfos,
    isSelected: Boolean,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageFile = remember(couleur.nomImageFichieSansEtansion, couleur.extensionDisponible) {
        if (couleur.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${couleur.nomImageFichieSansEtansion}.${couleur.extensionDisponible}"
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
                    contentDescription = couleur.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = !isSelected) {

                        },
                    contentScale = if (isSelected) ContentScale.Fit else ContentScale.Crop
                ) {
                    it.applyOptimizedImageOptions(couleur)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            if (!isSelected) {
                IconButton(
                    onClick = onIconClick,
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
