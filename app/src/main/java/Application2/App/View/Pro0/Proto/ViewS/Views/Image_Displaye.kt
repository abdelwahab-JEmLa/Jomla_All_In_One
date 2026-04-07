package Application2.App.View.Pro0.Proto.ViewS.Views

import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
    standart(350, 60) ,      // (400,70)
    min_possible
}

private fun resolveQualite(expandState: ProduitExpandState) = when {
    expandState.isExpanded -> ImageQualite.max_possible
    expandState.isAnyExpanded -> ImageQualite.min_possible
    else -> ImageQualite.standart
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye_app2(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
) {
    val qualite = resolveQualite(expandState)

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
        GlideImage(
            model = imageFile,
            contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
            modifier = modifier.fillMaxSize(),
            contentScale = contentScale
        ) { it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, qualite) }
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}

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
            ImageQualite.standart -> ImageQualite.standart.override
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
            ImageQualite.standart -> ImageQualite.standart.encodeQuality
            ImageQualite.min_possible -> 20
        }
    )
    .skipMemoryCache(qualite == ImageQualite.min_possible)
