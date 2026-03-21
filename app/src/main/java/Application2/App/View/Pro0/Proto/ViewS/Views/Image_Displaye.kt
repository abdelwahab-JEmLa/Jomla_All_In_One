package Application2.App.View.Pro0.Proto.ViewS.Views

import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import EntreApps.Shared.Models.M3CouleurProduitInfos
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
fun Image_Displaye_app2(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    onImageClick: (() -> Unit)? = null,
    // NEW: pass these in so the image can broadcast the toggle over WiFi when acting as host
    isHostPhone: Boolean = false,
    isConnected: Boolean = false,
    sendOrderToClientDisplayer: ((WifiUpdateClientDisplayerStats, Any?) -> Unit)? = null,
) {
    val imageFile = remember(
        relative_M3CouleurProduitInfos.nomImageFichieSansEtansion,
        relative_M3CouleurProduitInfos.extensionDisponible
    ) {
        if (relative_M3CouleurProduitInfos.nomImageFichieSansEtansion != "Non Dispo") {
            File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${relative_M3CouleurProduitInfos.nomImageFichieSansEtansion}.${relative_M3CouleurProduitInfos.extensionDisponible}"
            )
        } else null
    }

    if (imageFile != null && imageFile.exists()) {
        GlideImage(
            model = imageFile,
            contentDescription = relative_M3CouleurProduitInfos.nomCouleurStrSiSonImageDispo.ifBlank { "Color image" },
            modifier = modifier.fillMaxSize().clickable {
                when {
                    // Explicit override (e.g. from a parent that already knows what to do)
                    onImageClick != null -> onImageClick()

                    // Host connected to a client → toggle locally AND broadcast so the client mirrors it
                    isHostPhone && isConnected && sendOrderToClientDisplayer != null -> {
                        expandState.onImageTap(relative_M3CouleurProduitInfos)
                        sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran,
                            relative_M3CouleurProduitInfos.keyID
                        )
                    }

                    // Client connected to host OR standalone (no WiFi) → toggle locally only
                    else -> expandState.onImageTap(relative_M3CouleurProduitInfos)
                }
            },
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
