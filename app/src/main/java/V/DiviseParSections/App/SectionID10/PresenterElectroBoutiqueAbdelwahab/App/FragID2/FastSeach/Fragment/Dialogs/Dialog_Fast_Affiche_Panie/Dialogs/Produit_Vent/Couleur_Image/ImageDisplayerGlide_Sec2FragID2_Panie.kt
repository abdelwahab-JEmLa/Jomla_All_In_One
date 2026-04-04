package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.Couleur_Image

import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject
import java.io.File

@Composable
fun ColorNameDisplayer_FragFastVent(
    modifier: Modifier = Modifier,
    colorName: String,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colorName.ifEmpty { "No Color" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayerGlide_FragFastVent(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    relative_M3CouleurProduit: M3CouleurProduitInfos,
    colorName: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    colorFilter: ColorFilter? = null,
) {
    var isLoading by remember { mutableStateOf(true) }
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val actualImageFileName = M3CouleurProduitInfos.decrementing_file_name_si_non_trouve(
        relative_M3CouleurProduit.nomImageFichieSansEtansion,
        relative_M3CouleurProduit.extensionDisponible
    )

    val actualImageFile = actualImageFileName?.let {
        File(
            "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
            "$it.${relative_M3CouleurProduit.extensionDisponible}"
        )
    }

    val imageExists = actualImageFile != null

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.size(imageSize.width, imageSize.height)) {
            if (imageExists && actualImageFile != null) {
                GlideImage(
                    model = actualImageFile,
                    contentDescription = "Color image for $colorName",
                    contentScale = contentScale,
                    colorFilter = colorFilter,
                    modifier = Modifier
                        .clickable { }
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f)
                                renderEffect = BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                        }
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        // lastModified() changes automatically when syncFromImages2 calls
                        // file.setLastModified(dropBoxModMs) → Glide reloads the new file
                        signature(ObjectKey(actualImageFile.lastModified()))
                        listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?, model: Any?,
                                target: Target<Drawable>, isFirstResource: Boolean
                            ) = false

                            override fun onResourceReady(
                                resource: Drawable, model: Any,
                                target: Target<Drawable>?, dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (isFirstResource) isLoading = false
                                return false
                            }
                        })
                    }
                }
            } else {
                ColorNameDisplayer_FragFastVent(
                    modifier = Modifier.fillMaxSize(),
                    colorName = colorName,
                )
            }

            val activeCentralValues = focusedValuesGetter.active_Central_Values
            val afficheur_Panier_Pour_Link_M10OperationVentCouleur =
                activeCentralValues.handled_M10OperationVent_Pour_Link

            if (afficheur_Panier_Pour_Link_M10OperationVentCouleur != null) {
                SmallFloatingActionButton(
                    onClick = {
                        if (relative_M10OperationVentCouleur != null) {
                            val m1Produit = repositorysMainGetter.find_M1Produit_ByKeyID(
                                relative_M10OperationVentCouleur.parent_M1Produit_KeyId
                            )
                            afficheur_Panier_Pour_Link_M10OperationVentCouleur.copy(
                                its_Linked_To_Autre_Vent_Si_NonDispo = true,
                                linked_To_M10OperationVent_KeyID = relative_M10OperationVentCouleur.keyID,
                                linked_To_M10OperationVent_DebugInfos = relative_M10OperationVentCouleur.getDebugInfos(),
                                siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId = relative_M10OperationVentCouleur.parent_M3CouleurProduit_KeyID ?: "",
                                siNonDispoParentM10Vent_it_parent_M1Produit_Nom = m1Produit?.nom ?: ""
                            ).let {
                                aCentralFacade.repositorysMainSetter.update_M10OperationVentCouleur(it)
                            }
                        }
                        focusedValuesGetter.update_activeCentralValues(
                            activeCentralValues.copy(handled_M10OperationVent_Pour_Link = null)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .zIndex(1f),
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
