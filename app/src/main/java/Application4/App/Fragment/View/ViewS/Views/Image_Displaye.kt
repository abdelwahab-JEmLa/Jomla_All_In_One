@file:Suppress("DEPRECATION")

package Application4.App.Fragment.View.ViewS.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

enum class pourcentage {
    max_possible,
    standart,
    min_possible
}

@RequiresApi(Build.VERSION_CODES.Q)
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
    val isMainExpandedColor =
        centralValues.expanded_M3CouleurProduitInfos?.keyID == relative_M3CouleurProduitInfos.keyID
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
            .then(
                if (relative_M3CouleurProduitInfos.il_a_une_video_presentaion && isExpandedProduct && isMainExpandedColor) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxSize()
                }
            )
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
            if (isExpandedProduct && isMainExpandedColor) {
                val context = LocalContext.current
                val videoRatio = remember(imageFile) {
                    try {
                        MediaMetadataRetriever().use { mmr ->
                            mmr.setDataSource(imageFile.absolutePath)
                            val w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloatOrNull() ?: 9f
                            val h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloatOrNull() ?: 16f
                            val rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
                            if (rotation == 90 || rotation == 270) h / w else w / h
                        }
                    } catch (e: Exception) {
                        9f / 16f
                    }
                }
                val exoPlayer = remember(imageFile) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(Uri.fromFile(imageFile)))
                        prepare()
                        playWhenReady = true
                        repeatMode = Player.REPEAT_MODE_ONE
                        volume = 0f
                    }
                }
                DisposableEffect(exoPlayer) {
                    onDispose { exoPlayer.release() }
                }
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                        }
                    },
                    update = { view -> view.player = exoPlayer },
                    modifier = completeModifier.aspectRatio(videoRatio)
                )
            } else {
                Box(modifier = completeModifier) {
                    GlideImage(
                        model = imageFile,
                        contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo
                            .ifBlank { "Color video thumbnail" },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    ) {
                        it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, effectiveQuality)
                    }

                    if (isExpandedProduct) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Video available",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
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
            }
        } else {
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
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank {
                    "Image\nNon Dispo"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

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

private fun RequestBuilder<Drawable>.applyAnimatedGifOptions(
    couleur: M3CouleurProduitInfos
) = this
    .diskCacheStrategy(DiskCacheStrategy.DATA)
    .priority(Priority.HIGH)
    .signature(ObjectKey("${couleur.keyID}_gif_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(800)
    .disallowHardwareConfig()
    .format(DecodeFormat.PREFER_ARGB_8888)
    .skipMemoryCache(false)
