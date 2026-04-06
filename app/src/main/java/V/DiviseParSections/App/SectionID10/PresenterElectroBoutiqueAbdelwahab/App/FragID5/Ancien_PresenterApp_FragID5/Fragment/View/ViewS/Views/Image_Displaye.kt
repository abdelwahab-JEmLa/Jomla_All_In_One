package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.Views

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
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
import org.koin.compose.koinInject
import java.io.File

enum class pourcentage{
    max_possible,
    standart,
    min_possible
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),

    image_pourcetage_qualite : pourcentage= pourcentage.min_possible
) {    val imageFile = remember(
    relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
    relative_M3CouleurProduitInfos.extensionDisponible
) {
    if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
        val fileName =
            "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
        File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
    } else {
        null
    }
}

    if (imageFile != null && imageFile.exists()) {
        // FIXED: Get the parent product for this color
        val parentProduct = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
            repositorysMainGetter.repoM1Produit.datasValue.find {
                it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
            }
        }

        // Build the complete modifier with click handler BEFORE passing to GlideImage
        val completeModifier = modifier
            .fillMaxSize()
            .then(
                Modifier.clickable {
                    val currentExpandedProduct = focusedValuesGetter.active_Central_Values.expanded_M1Produit
                    val currentExpandedColor = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

                    // Check if we're clicking on a color from the SAME product that's already expanded
                    val isSameProductExpanded = currentExpandedProduct?.keyID == parentProduct?.keyID
                    val isDifferentColor = currentExpandedColor?.keyID != relative_M3CouleurProduitInfos.keyID

                    if (isSameProductExpanded && isDifferentColor) {
                        // CASE 1: Same product, different color → Update only the selected color
                        focusedValuesGetter.update_activeCentralValues(
                            focusedValuesGetter.active_Central_Values.copy(
                                expanded_M3CouleurProduitInfos = relative_M3CouleurProduitInfos
                                // Keep expanded_M1Produit unchanged
                            )
                        )
                    } else {
                        // CASE 2: Different product OR same color → Toggle product expansion
                        val newProductValue = if (currentExpandedProduct?.keyID == parentProduct?.keyID) {
                            // Same product, same color → Collapse
                            null
                        } else {
                            // Different product → Expand it
                            parentProduct
                        }

                        focusedValuesGetter.update_activeCentralValues(
                            focusedValuesGetter.active_Central_Values.copy(
                                expanded_M1Produit = newProductValue,
                                expanded_M3CouleurProduitInfos = if (newProductValue != null) {
                                    relative_M3CouleurProduitInfos
                                } else {
                                    null
                                }
                            )
                        )
                    }

                }
            )

        GlideImage(
            model = imageFile,
            contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
            modifier = completeModifier,
            contentScale = contentScale
        ) {
            it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos, image_pourcetage_qualite)
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
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
    .skipMemoryCache(false)
