package com.example.clientjetpack.App2.App.View.ViewS.Views

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.ViewModel.ViewModel_MainFragment
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye_app2(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    FocusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    relative_M1ProduitToItListM3Couleur: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
) {
    val imageFile = remember(
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
        val parentProduct = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
            relative_M1ProduitToItListM3Couleur.first
        }

        // Build the complete modifier with click handler BEFORE passing to GlideImage
        val completeModifier = modifier
            .fillMaxSize()
            .then(
                Modifier.clickable {
                    val currentExpandedProduct = FocusedValuesGetter_app2.active_Central_Values.expanded_M1Produit
                    val currentExpandedColor = FocusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos

                    // Check if we're clicking on a color from the SAME product that's already expanded
                    val isSameProductExpanded = currentExpandedProduct?.keyID == parentProduct?.keyID
                    val isDifferentColor = currentExpandedColor?.keyID != relative_M3CouleurProduitInfos.keyID

                    if (isSameProductExpanded && isDifferentColor) {
                        // CASE 1: Same product, different color → Update only the selected color
                        FocusedValuesGetter_app2.update_ActiveCentralValues_app2(
                            FocusedValuesGetter_app2.active_Central_Values.copy(
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

                        FocusedValuesGetter_app2.update_ActiveCentralValues_app2(
                            FocusedValuesGetter_app2.active_Central_Values.copy(
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
            it.applyOptimizedImageOptions(relative_M3CouleurProduitInfos)
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
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
    .format(DecodeFormat.PREFER_RGB_565)
    .encodeQuality(70)
    .skipMemoryCache(false)
