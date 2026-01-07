package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image_Displaye(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
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
        // Build the complete modifier with click handler BEFORE passing to GlideImage
        val completeModifier = modifier
            .fillMaxSize()
            .then(              //<--
            //TODO(2.C Relative Au Todo(1): 
                    //... pk il ya des produit quand je click a leur image le paretnt item ne devie pas FullLine
                Modifier.clickable {
                    val currentExpanded =
                        focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

                    val newValue =
                        if (currentExpanded?.keyID == relative_M3CouleurProduitInfos.keyID) {
                            null
                        } else {
                            relative_M3CouleurProduitInfos
                        }

                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            expanded_M3CouleurProduitInfos = newValue
                        )
                    )

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
