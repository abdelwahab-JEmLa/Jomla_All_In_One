package Application4.App.Fragment.View.ViewS.Views

import Application4.App.Fragment.A.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M3CouleurProduitInfos
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
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
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    on_pour_send_data: (String, String) -> Unit,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values

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
        // Sourced from uiState — no direct repository injection needed
        val parentProduct = remember(
            relative_M3CouleurProduitInfos.parentBProduitInfosKeyID,
            uiState.list_M1Produit
        ) {
            uiState.list_M1Produit.find {
                it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
            }
        }

        val completeModifier = modifier
            .fillMaxSize()
            .then(
                Modifier.clickable {
                    val currentExpandedProduct = centralValues.expanded_M1Produit
                    val currentExpandedColor = centralValues.expanded_M3CouleurProduitInfos

                    val isSameProductExpanded = currentExpandedProduct?.keyID == parentProduct?.keyID
                    val isDifferentColor = currentExpandedColor?.keyID != relative_M3CouleurProduitInfos.keyID

                    if (isSameProductExpanded && isDifferentColor) {
                        // CASE 1: Same product, different color → Update only the selected color
                        viewModel.update_activeCentralValues(
                            centralValues.copy(
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

                        viewModel.update_activeCentralValues(
                            centralValues.copy(
                                expanded_M1Produit = newProductValue,
                                expanded_M3CouleurProduitInfos = if (newProductValue != null) {
                                    relative_M3CouleurProduitInfos
                                } else {
                                    null
                                }
                            )
                        )
                    }

                    // Send the data update
                    on_pour_send_data(
                        WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran.prefix,
                        relative_M3CouleurProduitInfos.keyID
                    )
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
