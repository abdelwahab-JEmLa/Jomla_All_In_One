@file:Suppress("DEPRECATION")

package Application4.App.Fragment.View.ViewS.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import java.io.File

enum class pourcentage {
    max_possible,
    standart,
    min_possible
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye(
    modifier: Modifier = Modifier,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    contentScale: ContentScale = ContentScale.Fit,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    list_M1Produit: List<M01Produit>?,
    image_pourcetage_qualite: pourcentage = pourcentage.min_possible
) {
    val (_, viewModel) = uiState_NewProtoPatterns_viewModel
    val wifiState by viewModel.wifiState.collectAsState()
    val centralValues = wifiState

    val isExpandedProduct =
        centralValues.expanded_M1Produit?.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
    val isAnyExpanded = centralValues.expanded_M1Produit != null

    val effectiveQuality: pourcentage = when {
        isExpandedProduct -> pourcentage.max_possible
        isAnyExpanded     -> pourcentage.min_possible
        else              -> if (viewModel.active_Datas.filterAffichageMode_Proto == Filter_Affichage_Mode_Proto.Echants_Seulement) pourcentage.min_possible
        else image_pourcetage_qualite
    }

    val imageFile = remember(
        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
        relative_M3CouleurProduitInfos.extensionDisponible,
        relative_M3CouleurProduitInfos.parentBProduitOldID,
        relative_M3CouleurProduitInfos.indexCouleurDansAncienProto,
    ) {
        val baseDir = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName =
                "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            File(baseDir, fileName)
        } else {
            // Fallback: old-proto naming → "<parentBProduitOldID>_<indexCouleurDansAncienProto>.<ext>"
            val oldId    = relative_M3CouleurProduitInfos.parentBProduitOldID
            val colorIdx = relative_M3CouleurProduitInfos.indexCouleurDansAncienProto
            if (oldId > 0) {
                listOf("jpg", "png", "webp").map { ext ->
                    File(baseDir, "${oldId}_${colorIdx}.$ext")
                }.firstOrNull { it.exists() }
            } else {
                null
            }
        }
    }

    if (imageFile != null && imageFile.exists()) {
        val parentProduct = remember(
            relative_M3CouleurProduitInfos.parentBProduitInfosKeyID,
            list_M1Produit
        ) {
            list_M1Produit?.find {
                it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
            }
        }

        val completeModifier = modifier
            .fillMaxSize()
            .then(
                Modifier.clickable {
                    val isSameProductExpanded =
                        centralValues.expanded_M1Produit?.keyID == parentProduct?.keyID
                    val isSameColor =
                        centralValues.expanded_M3CouleurProduitInfos?.keyID == relative_M3CouleurProduitInfos.keyID

                    if (isSameProductExpanded && isSameColor) {
                        viewModel.updateExpandedProduitEtCouleur(null, null)
                    } else {
                        viewModel.updateExpandedProduitEtCouleur(
                            parentProduct,
                            relative_M3CouleurProduitInfos
                        )
                    }
                }
            )

        if (relative_M3CouleurProduitInfos.il_a_une_video_presentaion) {
            // ── GIF (vidéo convertie) ─────────────────────────────────────────
            // Expanded  → Coil + GifDecoder : animation GIF fluide, boucle infinie
            // Compact   → Glide dontAnimate() : 1ère frame comme vignette + icône play
            if (isExpandedProduct) {
                val context = LocalContext.current
                val gifLoader = remember(context) {
                    ImageLoader.Builder(context)
                        .components { add(GifDecoder.Factory()) }
                        .build()
                }
                val gifRequest = remember(
                    imageFile,
                    relative_M3CouleurProduitInfos.dernierTimeTampsSynchronisationAvecFireBase
                ) {
                    ImageRequest.Builder(context)                      //<--
                    //TODO(1): fait que si a un vid que si expand playe le utilise le lib le pplus facile et concervateur des ressources phone 
                        .data(imageFile)
                        .memoryCacheKey("${relative_M3CouleurProduitInfos.keyID}_gif_${relative_M3CouleurProduitInfos.dernierTimeTampsSynchronisationAvecFireBase}")
                        .crossfade(false)
                        .build()
                }
                AsyncImage(
                    model          = gifRequest,
                    imageLoader    = gifLoader,
                    contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo
                        .ifBlank { "Color video presentation as GIF" },
                    modifier       = completeModifier,
                    contentScale   = contentScale,
                )
            } else {
                // Compact: Glide extrait la 1ère frame (dontAnimate) + icône play
                Box(modifier = completeModifier) {
                    GlideImage(
                        model = imageFile,
                        contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo
                            .ifBlank { "Color video thumbnail" },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    ) {
                        it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, pourcentage.min_possible)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else {
            // ── Image statique → Glide uniquement ────────────────────────────
            GlideImage(
                model = imageFile,
                contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo
                    .ifBlank { "Color image" },
                modifier = completeModifier,
                contentScale = contentScale
            ) {
                it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, effectiveQuality)
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}

/** Images statiques : animation supprimée, priorité et qualité selon pourcentage. */
private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos,
    qualite: pourcentage
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(when (qualite) {
        pourcentage.max_possible -> Priority.HIGH
        pourcentage.standart     -> Priority.NORMAL
        pourcentage.min_possible -> Priority.LOW
    })
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(when (qualite) {
        pourcentage.max_possible -> 800
        pourcentage.standart     -> 300
        pourcentage.min_possible -> 150
    })
    .disallowHardwareConfig()
    .format(when (qualite) {
        pourcentage.max_possible -> DecodeFormat.PREFER_ARGB_8888
        pourcentage.standart     -> DecodeFormat.PREFER_RGB_565
        pourcentage.min_possible -> DecodeFormat.PREFER_RGB_565
    })
    .encodeQuality(when (qualite) {
        pourcentage.max_possible -> 100
        pourcentage.standart     -> 50
        pourcentage.min_possible -> 20
    })
    .skipMemoryCache(qualite == pourcentage.min_possible)
