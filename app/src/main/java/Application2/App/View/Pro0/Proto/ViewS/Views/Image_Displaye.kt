package Application2.App.View.Pro0.Proto.ViewS.Views

import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
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
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import java.io.File

enum class ImageQualite(
    val encodeQuality: Int = 0,
    val override: Int = 0
) {
    max_possible,
    standart(400, 70) ,      // (400,70)
    min_possible
}

private fun resolveQualite(expandState: ProduitExpandState) = when {
    expandState.isExpanded -> ImageQualite.max_possible
    expandState.isAnyExpanded -> ImageQualite.min_possible
    else -> ImageQualite.standart
}

/** For static images: suppress animation, apply quality/size overrides. */
private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos,
    qualite: ImageQualite,
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(
        when (qualite) {
            ImageQualite.max_possible -> Priority.HIGH
            ImageQualite.standart -> Priority.NORMAL
            ImageQualite.min_possible -> Priority.LOW
        }
    )
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(
        when (qualite) {
            ImageQualite.max_possible -> 800
            ImageQualite.standart -> 350
            ImageQualite.min_possible -> 150
        }
    )
    .disallowHardwareConfig()
    .format(
        when (qualite) {
            ImageQualite.max_possible -> DecodeFormat.PREFER_ARGB_8888
            ImageQualite.standart -> DecodeFormat.PREFER_RGB_565
            ImageQualite.min_possible -> DecodeFormat.PREFER_RGB_565
        }
    )
    .encodeQuality(
        when (qualite) {
            ImageQualite.max_possible -> 100
            ImageQualite.standart -> 50
            ImageQualite.min_possible -> 20
        }
    )
    .skipMemoryCache(qualite == ImageQualite.min_possible)

/** For animated GIFs in expanded mode: allow looping animation, full quality. */
private fun RequestBuilder<Drawable>.applyAnimatedGifOptions(
    couleur: M3CouleurProduitInfos
) = this
    // Do NOT call dontAnimate() here — animation must run
    .diskCacheStrategy(DiskCacheStrategy.DATA)
    .priority(Priority.HIGH)
    .signature(ObjectKey("${couleur.keyID}_gif_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(800)
    .disallowHardwareConfig()
    .format(DecodeFormat.PREFER_ARGB_8888)
    .skipMemoryCache(false)

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye_app2(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    viewModel: ViewModel_MainFragment,
) {
    val qualite = resolveQualite(expandState)

    // Get WiFi state to determine if user can interact with images
    val wifiState by viewModel.wifiState.collectAsState()
    val canInteract = wifiState.isHostPhone || !wifiState.isConnected

    val imageFile = remember(
        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
        relative_M3CouleurProduitInfos.extensionDisponible
    ) {
        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo")
            File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            )
        else null
    }

    if (imageFile != null && imageFile.exists()) {
        val completeModifier = modifier
            .fillMaxSize()
            .then(
                if (canInteract) {
                    Modifier.clickable {
                        viewModel.wifi.toggleExpandedCouleur(relative_M3CouleurProduitInfos)
                    }
                } else {
                    Modifier
                }
            )

        if (relative_M3CouleurProduitInfos.il_a_une_video_presentaion) {
            if (expandState.isExpanded) {
                // Expanded: play the GIF — do NOT use dontAnimate()
                GlideImage(
                    model = imageFile,
                    contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color GIF" },
                    modifier = completeModifier,
                    contentScale = contentScale
                ) { it.applyAnimatedGifOptions(relative_M3CouleurProduitInfos) }
            } else {
                // Compact: static first frame + play icon
                Box(modifier = completeModifier) {
                    GlideImage(
                        model = imageFile,
                        contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color thumbnail" },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    ) { it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, ImageQualite.min_possible) }
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
            GlideImage(
                model = imageFile,
                contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
                modifier = completeModifier,
                contentScale = contentScale
            ) { it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, qualite) }
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}
